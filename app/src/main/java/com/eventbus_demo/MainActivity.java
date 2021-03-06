package com.eventbus_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 使用 EventBus 依赖库
 */
public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        // 设置点击事件, 点击后发送消息
        textView.setOnClickListener((View view)->{
            EventBus.getDefault().post("Hello EventBus !");
        });

        // 首先注册订阅 EventBus
        EventBus.getDefault().register(this);


    }

    /**
     * 使用 @Subscribe 注解修饰处理消息的方法
     *      该方法必须是 public void 修饰的
     *      只有一个参数 , 参数类型随意
     *      调用 EventBus.getDefault().post 即可发送消息到该方法进行处理
     * @param msg
     */
    @Subscribe
    public void onMessgeEvent(String msg){
        textView.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册
        EventBus.getDefault().unregister(this);
    }
    /**
     * 注册给定订阅服务器以接收事件。订阅者一旦对接收事件不再感兴趣，就必须调用{@link#unregister（Object）}。
     * <p/>
     * 订阅服务器具有必须由{@link Subscribe}注释的事件处理方法。
     * {@link Subscribe}注释还允许类似{@link ThreadMode}和优先级的配置。
     */




}


