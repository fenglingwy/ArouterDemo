package com.wy.arouterdemo;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * @author WY
 * @date 2018/7/10
 * Description
 */
// 声明接口,其他组件通过接口来调用服务
public interface HelloService extends IProvider {
    String sayHello(String name);
}

