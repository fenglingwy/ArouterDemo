package com.wy.arouterdemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;


// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx
@Route(path = "/test/activity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);
    }


    /**
     *   普通跳转
     */
    public void startActivity(View view) {

        // 跳转并携带参数,参数优先级Url高
        Uri uri = Uri.parse("arouter://wy.com/test/secondActivity?key1=123&key3=qwe");
        ARouter.getInstance().build(uri)
//        ARouter.getInstance().build("/test/secondActivity")
                .withString("key2", "key2")
                .withString("key3", "888")
                .withTransition(R.anim.activity_exchange_right_in, R.anim.activity_exchange_left_out)//跳转动画
                .navigation(this);

    }

    /**
     * 跳转ForResult
     * @param view
     */
    public void startActivityForResult(View view) {
        ARouter.getInstance().build("/test/secondActivity").navigation(this,100);
    }


    /**
     * 跳转至module
     * @param view
     */
    public void startModuleActivity(View view) {
        ARouter.getInstance().build("/module/sub").navigation();
    }


    /**
     * 跳转使用Url
     * @param view
     */
    public void toWebView(View view) {
        ARouter.getInstance().build("/test/web").navigation();
    }

    @Autowired(name = "/service/hello")
    HelloService helloService1;

    /**
     * 服务
     * @param view
     */
    public void testService(View view){
        // 1. (推荐)使用依赖注入的方式发现服务,通过注解标注字段,即可使用，无需主动获取
        // Autowired注解中标注name之后，将会使用byName的方式注入对应的字段，不设置name属性，会默认使用byType的方式发现服务(当同一接口有多个实现的时候，必须使用byName的方式发现服务)
        helloService1.sayHello("helloService1");

        // 2. 使用依赖查找的方式发现服务，主动去发现服务并使用，下面两种方式分别是byName和byType
        HelloService navigation = (HelloService) ARouter.getInstance().build("/service/hello").navigation();
        HelloService navigation2 = ARouter.getInstance().navigation(HelloService.class);
        navigation.sayHello("byName");
        navigation2.sayHello("byType");
    }


    public void callback(View view) {
        ARouter.getInstance()
                .build("/test/secondActivity")
                .navigation(this, new NavCallback() {

                    @Override
                    public void onFound(Postcard postcard) {
                        Log.e("tag", "onArrival: 找到了 ");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        Log.e("tag", "onArrival: 找不到了 ");
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        Log.e("tag", "onArrival: 跳转完了 ");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        Log.e("tag", "onArrival: 被拦截了 ");
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,"requestCode=:"+requestCode+";resultCode=:"+resultCode,Toast.LENGTH_SHORT).show();
    }
}
