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
        helloService1.sayHello("helloService1");

        HelloService navigation = (HelloService) ARouter.getInstance().build("/service/hello").navigation();
        navigation.sayHello("build");

        HelloService navigation2 = ARouter.getInstance().navigation(HelloService.class);
        navigation2.sayHello("HelloService.class");
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
