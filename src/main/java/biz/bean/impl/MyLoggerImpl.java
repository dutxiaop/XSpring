package biz.bean.impl;

import biz.bean.MyLogger;
import biz.bean.TestInterface;
import com.alibaba.fastjson.JSONObject;
import lib.annotations.XComponent;

/**
 * Created by xiaoP on 2017/2/25.
 */
@XComponent
public class MyLoggerImpl implements MyLogger {

    TestInterface testInterface;

    public MyLoggerImpl(TestInterface testInterface) {
        this.testInterface = testInterface;
    }

    @Override
    public void print(Object... objects) {
        System.out.println("MyLogger: " + JSONObject.toJSONString(objects) + "\n" + testInterface.say());
    }
}
