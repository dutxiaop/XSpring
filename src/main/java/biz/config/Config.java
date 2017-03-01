package biz.config;

import lib.config.BeanConfig;
import biz.bean.impl.TestInterfaceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class Config {
    public static List<BeanConfig> beanConfigs = new ArrayList<>();

    static {
//        beanConfigs.add(new BeanConfig("logger", MyLoggerImpl.class.getName(), null));
        beanConfigs.add(new BeanConfig("test", TestInterfaceImpl.class.getName(), "init"));
    }
}
