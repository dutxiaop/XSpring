package main;

import lib.Utils.ExceptionHandler;
import lib.builder.BeanBuilder;
import biz.bean.TestInterface;

/**
 * Created by xiaoP on 2017/2/24.
 */
public class Main {
    public static void main(String args[]) {
        System.out.println("wang da zhuang");
        BeanBuilder beanBuilder = new BeanBuilder();
        ExceptionHandler.throwException(() ->
                beanBuilder.initBean()
        );

//        ExceptionHandler.ignore(() -> ((TestInterface) beanBuilder.getBean("xx")).test());
        ExceptionHandler.ignore(() -> ((TestInterface) beanBuilder.getBean("test")).test());
        ExceptionHandler.ignore(() -> ((TestInterface) beanBuilder.getBean(TestInterface.class)).test());
    }
}
