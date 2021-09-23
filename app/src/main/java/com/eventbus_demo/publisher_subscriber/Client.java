package com.eventbus_demo.publisher_subscriber;

public class Client {
    public static void main(String[] args) {
        // 创建订阅者
        Subscriber1 subscriber1 = new Subscriber1();
        Subscriber2 subscriber2 = new Subscriber2();

        // 注册订阅者
        Dispatcher.getInstance().register(subscriber1);
        Dispatcher.getInstance().register(subscriber2);

        // 创建发布者
        Publisher publisher = new Publisher();
        // 发布消息
        publisher.post("Hello");
    }
}