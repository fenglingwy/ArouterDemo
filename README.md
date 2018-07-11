[GitHub链接](https://github.com/alibaba/ARouter)

```
    一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦
```



[Demo]( https://github.com/fenglingwy/ArouterDemo.git)
#### 使用案例
1. 添加依赖和配置
``` gradle
android {
    defaultConfig {
	...
	javaCompileOptions {
	    annotationProcessorOptions {
		arguments = [ moduleName : project.getName() ]
	    }
	}
    }
}

dependencies {
    api 'com.alibaba:arouter-api:1.3.1'
    annotationProcessor 'com.alibaba:arouter-compiler:1.1.3'
    ...
}


//gradle 插件实现路由表的自动加载
apply plugin: 'com.alibaba.arouter'

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath "com.alibaba:arouter-register:1.0.2"
    }
}

```

> 注意依赖包的版本是否匹配  
最新版api-1.3.1和compiler-1.1.4使用@Autowired会出现获取不到参数问题


2. 初始化SDK
``` java
if (BuildConfig.DEBUG) {// 这两行必须写在init之前，否则这些配置在init过程中将无效
    ARouter.openLog();     // 打印日志
    ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
}
ARouter.init(mApplication); // 尽可能早，推荐在Application中初始化
```

3. 添加注解
``` java
// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx
@Route(path = "/test/secondActivity")
public class SecondActivity extends AppCompatActivity {
    ...
}
```

4. 发起路由操作
``` java
        
    // 通过path跳转   
   ARouter.getInstance().build("/test/secondActivity")
        .withString("key2", "key2")
        .withString("key3", "888")
        .navigation();

    //通过Url跳转跳转并携带参数,Url参数优先级高
   Uri uri = Uri.parse("arouter://wy.com/test/secondActivity?key1=123&key3=qwe");
        ARouter.getInstance().build(uri)
                .withString("key2", "key2")
                .withString("key3", "888")
                .withTransition(R.anim.activity_exchange_right_in, R.anim.activity_exchange_left_out)//跳转动画
                .navigation(this);
```




5. 通过URL跳转
``` java
// 新建一个Activity用于监听Schame事件,之后直接把url传递给ARouter即可
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Uri uri = getIntent().getData();
	ARouter.getInstance().build(uri).navigation();
	finish();
    }
}
```

AndroidManifest.xml
``` xml
 <activity android:name=".SecondActivity">
            <intent-filter>
                <data
                    android:host="wy.com"
                    android:scheme="arouter" />

                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>
</activity>
```

6. 解析URL中的参数
``` java
    //1
    ARouter.getInstance().inject(this);

    //2
    @Autowired
    public String name;
```

7. 声明拦截器(拦截跳转过程，面向切面编程)
``` java
// 比较经典的应用就是在跳转过程中处理登陆事件，这样就不需要在目标页重复做登陆检查
// 拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行
@Interceptor(priority = 8, name = "登录拦截器")
public class TestInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {

        Log.d("tag","登陆成功！");

        callback.onContinue(postcard);  // 处理完成，交还控制权
        //callback.onInterrupt(new RuntimeException("我觉得有点异常"));// 觉得有问题，中断路由流程

        // 以上两种至少需要调用其中一种，否则不会继续路由
    }

    @Override
    public void init(Context context) {
        // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
    }
}
```

8. 处理跳转结果
``` java
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

    
```

9. 自定义全局降级策略
``` java
// 实现DegradeService接口，并加上一个Path内容任意的注解即可
@Route(path = "/xxx/xxx")
public class DegradeServiceImpl implements DegradeService {
  @Override
  public void onLost(Context context, Postcard postcard) {
	// do something.
  }

  @Override
  public void init(Context context) {

  }
}
```

10. 为目标页面声明更多信息
``` java
// 我们经常需要在目标页面中配置一些属性，比方说"是否需要登陆"之类的
// 可以通过 Route 注解中的 extras 属性进行扩展，这个属性是一个 int值，换句话说，单个int有4字节，也就是32位，可以配置32个开关
// 剩下的可以自行发挥，通过字节操作可以标识32个开关，通过开关标记目标页面的一些属性，在拦截器中可以拿到这个标记进行业务逻辑判断
@Route(path = "/test/activity", extras = Consts.XXXX)
```

11. 通过依赖注入解耦:服务管理(一) 暴露服务
``` java
// 声明接口,其他组件通过接口来调用服务
public interface HelloService extends IProvider {
    String sayHello(String name);
}

// 实现接口
@Route(path = "/service/hello", name = "测试服务")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
	return "hello, " + name;
    }

    @Override
    public void init(Context context) {

    }
}
```

12. 通过依赖注入解耦:服务管理(二) 发现服务
``` java
// 1. (推荐)使用依赖注入的方式发现服务,通过注解标注字段,即可使用，无需主动获取
// Autowired注解中标注name之后，将会使用byName的方式注入对应的字段，不设置name属性，会默认使用byType的方式发现服务(当同一接口有多个实现的时候，必须使用byName的方式发现服务)
helloService1.sayHello("helloService1");

// 2. 使用依赖查找的方式发现服务，主动去发现服务并使用，下面两种方式分别是byName和byType
HelloService navigation = (HelloService) ARouter.getInstance().build("/service/hello").navigation();
HelloService navigation2 = ARouter.getInstance().navigation(HelloService.class);
navigation.sayHello("byName");
navigation2.sayHello("byType");
```

