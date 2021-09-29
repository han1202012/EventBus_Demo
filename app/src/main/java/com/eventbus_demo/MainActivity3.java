package com.eventbus_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eventbus_demo.myeventbus.MyEventBus;
import com.eventbus_demo.myeventbus.MySubscribe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 使用 MyEventBus 依赖库
 */
public class MainActivity3 extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        // 设置点击事件, 点击后发送消息
        textView.setOnClickListener((View view)->{
            MyEventBus.getInstance().post("Hello EventBus !");
        });

        // 首先注册订阅 EventBus
        MyEventBus.getInstance().register(this);
    }

    /**
     * 使用 @Subscribe 注解修饰处理消息的方法
     *      该方法必须是 public void 修饰的
     *      只有一个参数 , 参数类型随意
     *      调用 EventBus.getDefault().post 即可发送消息到该方法进行处理
     * @param msg
     */
    @MySubscribe
    public void onMessgeEvent(String msg){
        textView.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册
        MyEventBus.getInstance().unregister(this);
    }
}


