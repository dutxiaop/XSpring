package biz.bean.impl;

import biz.bean.MyLogger;
import biz.bean.TestInterface;
import lib.annotations.XAutowired;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class TestInterfaceImpl implements TestInterface {

    private MyLogger logger;

    public TestInterfaceImpl() {
        System.out.println("TestInterfaceImpl empty");
    }

    @XAutowired
    public TestInterfaceImpl(MyLogger logger) {
        this.logger = logger;
        System.out.println("TestInterfaceImpl logger");
    }

    public void init() {
        System.out.println(this.getClass().getName() + " : init.");
    }

    public void test() {
        logger.print(this.getClass().getName() + " : test.");
    }

    @Override
    public String say() {
        return "say nothing";
    }

    public MyLogger getLogger() {
        return logger;
    }

    public void setLogger(MyLogger logger) {
        this.logger = logger;
    }
}
