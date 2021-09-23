package com.eventbus_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
}

