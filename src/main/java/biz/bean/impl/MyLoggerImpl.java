package biz.bean.impl;

import lib.annotations.XComponent;
import biz.bean.MyLogger;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by xiaoP on 2017/2/25.
 */
@XComponent
public class MyLoggerImpl implements MyLogger {
    @Override
    public void print(Object... objects) {
        System.out.println("MyLogger: " + JSONObject.toJSONString(objects));
    }
}
