package lib.builder;

import biz.config.Config;
import lib.Utils.ExceptionHandler;
import lib.Utils.StringUtils;
import lib.Utils.exceptions.BeanNotFoundException;
import lib.Utils.exceptions.MultiClassFoundException;
import lib.annotations.XAutowired;
import lib.annotations.XComponent;
import lib.config.BeanConfig;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class BeanBuilder {
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, BeanConfig> beanConfigMap = new HashMap<>();

    public void initBean() {
        List<BeanConfig> configs = initConfig();
        generateBeans(configs);
        afterGenerate();
    }

    private void afterGenerate() {
        beans.entrySet().forEach(bean -> {
            ExceptionHandler.throwException(() -> {
                setFieldValue(bean.getValue().getClass(), bean.getValue());
                calInitMethod(beanConfigMap.get(bean.getKey()), bean.getValue().getClass(), bean.getValue());
            });
        });
    }

    private void generateBeans(List<BeanConfig> configs) {
        configs.forEach(config -> {
            ExceptionHandler.throwException(() -> {
                Class aClass = Class.forName(config.getClassName());
                Object obj = getInstance(aClass);
                beans.put(config.getId(), obj);
            });
        });
    }

    private List<BeanConfig> initConfig() {
        List<BeanConfig> configs = getBeanConfigs();
        beanConfigMap.putAll(configs.stream()
                .collect(Collectors.toMap(BeanConfig::getId, Function.identity())));
        return configs;
    }

    private Object getInstance(Class aClass) throws InstantiationException, IllegalAccessException {
        return aClass.newInstance();
    }

    private List<BeanConfig> getBeanConfigs() {

        Reflections reflections = new Reflections("biz.");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(XComponent.class);
        Config.beanConfigs.addAll(
//                0,
                types.stream().map(t -> new BeanConfig(t.getSimpleName(), t.getName()))
                        .collect(Collectors.toList()));
        return Config.beanConfigs;
    }

    private void calInitMethod(BeanConfig config, Class aClass, Object obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!StringUtils.isEmpty(config.getInitMethod())) {
            Method method = aClass.getDeclaredMethod(config.getInitMethod());
            method.setAccessible(true);
            method.invoke(obj);
        }
    }

    private void setFieldValue(Class aClass, Object obj) throws ClassNotFoundException, MultiClassFoundException, IllegalAccessException {
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getDeclaredAnnotation(XAutowired.class) != null) {
                Object fieldObj = getBean(field.getType());
                field.setAccessible(true);
                field.set(obj, fieldObj);
            }
        }
    }

    public Object getBean(String id) throws BeanNotFoundException {
        Object obj = beans.get(id);
        if (obj == null) {
            throw new BeanNotFoundException("bean [" + id + "] not found.");
        }
        return obj;
    }

    public Object getBean(Class clazz) throws ClassNotFoundException, MultiClassFoundException {
        List<Object> list = beans.values().stream().filter(o -> {
            if (o.getClass().getName().equals(clazz.getName())) {
                return true;
            }
            Class<?>[] interfaces = o.getClass().getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                for (Class<?> aInterface : interfaces) {
                    if (aInterface.getName().equals(clazz.getName()))
                        return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        if (list.size() == 0) {
            throw new ClassNotFoundException();
        }
        if (list.size() > 1) {
            throw new MultiClassFoundException("multi class found.");
        }
        return list.get(0);
    }
}
