package com.wy.arouterdemo;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * @author WY
 * @date 2018/7/10
 * Description
 */
// 实现接口
@Route(path = "/service/hello", name = "测试服务")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        Log.d("tag","hello, " + name);
        return "hello, " + name;
    }

    @Override
    public void init(Context context) {

    }
}