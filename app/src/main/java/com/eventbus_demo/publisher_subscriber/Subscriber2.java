package com.eventbus_demo.publisher_subscriber;

public class Subscriber2 implements Subscriber {
    @Override
    public void onEvent(String msg) {
        System.out.println("Subscriber2 订阅者收到消息 " + msg);
    }
}