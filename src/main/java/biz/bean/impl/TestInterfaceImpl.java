package biz.bean.impl;

import lib.annotations.XAutowired;
import biz.bean.MyLogger;
import biz.bean.TestInterface;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class TestInterfaceImpl implements TestInterface {

    @XAutowired
    private MyLogger logger;

    public void init(){
        System.out.println(this.getClass().getName() + " : init." );
    }

    public void test() {
        logger.print(this.getClass().getName() + " : test.");
    }

    public MyLogger getLogger() {
        return logger;
    }

    public void setLogger(MyLogger logger) {
        this.logger = logger;
    }
}
