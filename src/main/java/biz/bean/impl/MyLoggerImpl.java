package biz.bean.impl;

import biz.bean.MyLogger;
import biz.bean.TestInterface;
import com.alibaba.fastjson.JSONObject;
import lib.annotations.XAutowired;
import lib.annotations.XComponent;
import lib.annotations.XQualifier;

/**
 * Created by xiaoP on 2017/2/25.
 */
@XComponent("myLogger")
public class MyLoggerImpl implements MyLogger {

    @XAutowired
    @XQualifier("test")
    TestInterface testInterface;

    public MyLoggerImpl() {
    }

    public MyLoggerImpl(TestInterface testInterface) {
        this.testInterface = testInterface;
    }

    @Override
    public void print(Object... objects) {
        System.out.println("MyLogger: " + JSONObject.toJSONString(objects) + "\n" + testInterface.say());
    }
}
