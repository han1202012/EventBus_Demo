package com.eventbus_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eventbus_demo.publisher_subscriber.Dispatcher;
import com.eventbus_demo.publisher_subscriber.Publisher;
import com.eventbus_demo.publisher_subscriber.Subscriber;

public class MainActivity2 extends AppCompatActivity implements Subscriber {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        // 设置点击事件, 点击后发送消息
        textView.setOnClickListener((View view)->{
            // 发布者发布消息
            new Publisher().post("Hello");
        });

        // 注册订阅者
        Dispatcher.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册订阅者
        Dispatcher.getInstance().unregister(this);
    }

    @Override
    public void onEvent(String msg) {
        Toast.makeText(
                this,
                "订阅者 Activity 接收到消息 : " + msg,
                Toast.LENGTH_LONG).show();
    }
}

