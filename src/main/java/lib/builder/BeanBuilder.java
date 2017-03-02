package lib.builder;

import biz.config.Config;
import com.google.common.collect.Sets;
import lib.Utils.ExceptionHandler;
import lib.Utils.StringUtils;
import lib.exceptions.BeanInBuildingException;
import lib.exceptions.BeanNotFoundException;
import lib.exceptions.MultiClassFoundException;
import lib.exceptions.MultiConstructorAnnotationException;
import lib.annotations.XAutowired;
import lib.annotations.XComponent;
import lib.config.BeanConfig;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class BeanBuilder {

    private Map<String, BeanConfig> beanConfigMap = new HashMap<>();

    public void initBean() throws ClassNotFoundException {
        List<BeanConfig> configs = initConfig();
        generateBeans(configs);
        afterGenerate();
    }

    private List<BeanConfig> initConfig() throws ClassNotFoundException {
        List<BeanConfig> configs = getBeanConfigs();
        beanConfigMap.putAll(configs.stream()
                .collect(Collectors.toMap(BeanConfig::getId, Function.identity())));
        return configs;
    }

    private List<BeanConfig> getBeanConfigs() throws ClassNotFoundException {
        Reflections reflections = new Reflections("biz.");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(XComponent.class);
        Config.beanConfigs.addAll(types.stream()
                .map(t -> new BeanConfig(t.getSimpleName(), t.getName()))
                .collect(Collectors.toList()));
        //set class
        for (BeanConfig config : Config.beanConfigs) {
            config.setClazz(Class.forName(config.getClassName()));
        }
        return Config.beanConfigs;
    }

    private void generateBeans(List<BeanConfig> configs) {
        Set<String> idSet = configs.stream().map(BeanConfig::getId).collect(Collectors.toSet());
        while (idSet.size() > 0) {
            BeanConfig config = beanConfigMap.get(idSet.iterator().next());
            ExceptionHandler.throwException(() -> {
                Set<String> buildingBeans = Sets.newHashSet();
                Set<String> buildBeans = Sets.newHashSet();
                generateInstanceFromBeanConfig(config, buildingBeans, buildBeans);
                idSet.removeAll(buildBeans);
            });
        }
    }

    private Object generateInstanceFromBeanConfig(BeanConfig beanConfig, Set<String> buildingBeans, Set<String> buildBeans) throws IllegalAccessException, InvocationTargetException, MultiClassFoundException, InstantiationException, MultiConstructorAnnotationException, ClassNotFoundException, BeanInBuildingException {
        if (beanConfig.getObject() == null) {
            if (buildingBeans.contains(beanConfig.getId())) {
                throw new BeanInBuildingException(beanConfig.toString());
            }
            buildingBeans.add(beanConfig.getId());
            Object object = generateInstance(beanConfig.getClazz(), buildingBeans, buildBeans);
            buildingBeans.remove(beanConfig.getId());
            buildBeans.add(beanConfig.getId());
            beanConfig.setObject(object);
        }
        return beanConfig.getObject();
    }

    private void calInitMethod(BeanConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!StringUtils.isEmpty(config.getInitMethod())) {
            Method method = config.getClazz().getDeclaredMethod(config.getInitMethod());
            method.setAccessible(true);
            method.invoke(config.getObject());
        }
    }

    private void setFieldValue(Class aClass, Object obj) throws ClassNotFoundException, MultiClassFoundException, IllegalAccessException, InstantiationException, BeanInBuildingException, MultiConstructorAnnotationException, InvocationTargetException {
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getDeclaredAnnotation(XAutowired.class) != null) {
                Object fieldObj = getBean(field.getType(), Sets.newHashSet(), Sets.newHashSet());
                field.setAccessible(true);
                field.set(obj, fieldObj);
            }
        }
    }

    public Object getBean(String id) throws IllegalAccessException, InvocationTargetException, MultiClassFoundException, BeanNotFoundException, InstantiationException, BeanInBuildingException, MultiConstructorAnnotationException, ClassNotFoundException {
        return getBean(id, null, null);
    }

    private Object getBean(String id, Set<String> buildingBeans, Set<String> buildBeans) throws BeanNotFoundException, IllegalAccessException, MultiConstructorAnnotationException, MultiClassFoundException, InstantiationException, InvocationTargetException, ClassNotFoundException, BeanInBuildingException {
        BeanConfig beanConfig = beanConfigMap.get(id);
        if (beanConfig == null) {
            throw new BeanNotFoundException("bean [" + id + "] not found.");
        }
        return getInstanceByBeanConfig(beanConfig, buildingBeans, buildBeans);
    }

    private Object getInstanceByBeanConfig(BeanConfig beanConfig, Set<String> buildingBeans, Set<String> buildBeans) throws IllegalAccessException, InvocationTargetException, MultiClassFoundException, InstantiationException, MultiConstructorAnnotationException, ClassNotFoundException, BeanInBuildingException {
        return beanConfig.getObject() != null ?
                beanConfig.getObject() : generateInstanceFromBeanConfig(beanConfig,
                buildingBeans == null ? Sets.newHashSet() : buildingBeans,
                buildBeans == null ? Sets.newHashSet() : buildBeans);
    }

    public Object getBean(Class clazz) throws IllegalAccessException, MultiConstructorAnnotationException, MultiClassFoundException, InstantiationException, BeanInBuildingException, InvocationTargetException, ClassNotFoundException {
        return getBean(clazz, null, null);
    }

    private Object getBean(Class clazz, Set<String> buildingBeans, Set<String> buildBeans) throws ClassNotFoundException, MultiClassFoundException, InvocationTargetException, InstantiationException, MultiConstructorAnnotationException, IllegalAccessException, BeanInBuildingException {
        List<BeanConfig> beanConfigList = beanConfigMap.values().stream()
                .filter(config -> clazz.isAssignableFrom(config.getClazz()))
                .collect(Collectors.toList());

        if (beanConfigList.size() == 0) {
            throw new ClassNotFoundException();
        }
        if (beanConfigList.size() > 1) {
            throw new MultiClassFoundException("multi class found.");
        }
        return getInstanceByBeanConfig(beanConfigList.get(0), buildingBeans, buildBeans);
    }


    private Object generateInstance(Class clazz, Set<String> buildingBeans, Set<String> buildBeans) throws InstantiationException, IllegalAccessException, MultiConstructorAnnotationException, InvocationTargetException, MultiClassFoundException, ClassNotFoundException, BeanInBuildingException {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 0) {
            Constructor annotationConstructor = null;
            for (Constructor constructor : constructors) {
                if (constructor.getDeclaredAnnotation(XAutowired.class) != null) {
                    if (annotationConstructor == null) {
                        annotationConstructor = constructor;
                    } else {
                        throw new MultiConstructorAnnotationException(clazz.getName()
                                + " has more than 1 constructors with Autowired annotation.");
                    }
                }
            }
            if (annotationConstructor != null) {
                Class[] params = annotationConstructor.getParameterTypes();
                Object[] paramObjects = new Object[params.length];
                for (int i = 0; i < params.length; ++i) {
                    paramObjects[i] = getBean(params[i], buildingBeans, buildBeans);
                }
                return annotationConstructor.newInstance(paramObjects);
            }
        }
        return clazz.newInstance();
    }

    private void afterGenerate() {
        beanConfigMap.entrySet().forEach(beanConfig -> {
            ExceptionHandler.throwException(() -> {
                Object instance = beanConfig.getValue().getObject();
                setFieldValue(instance.getClass(), instance);
                calInitMethod(beanConfigMap.get(beanConfig.getKey())
                );
            });
        });
    }
}
