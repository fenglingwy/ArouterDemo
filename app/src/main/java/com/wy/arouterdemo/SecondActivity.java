package com.wy.arouterdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author WY
 * @date 2018/7/9
 * Description
 */

// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx
@Route(path = "/test/secondActivity")
public class SecondActivity extends AppCompatActivity {


    @Autowired
    public Long key1;

    @Autowired(name = "key3")
    public String key31111;

    @Autowired
    public String key2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);

        setContentView(R.layout.activity_s);


        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null)
            Log.i("tag", data.toString());

//        String key3 = intent.getStringExtra("key3");
//        long key1 = intent.getLongExtra("key1", 0);
        Toast.makeText(this, key31111 + "  "+key2+"  " + key1, Toast.LENGTH_SHORT).show();
    }


    public void onClick(View view) {
        setResult(12,new Intent());
        finish();
    }


}
