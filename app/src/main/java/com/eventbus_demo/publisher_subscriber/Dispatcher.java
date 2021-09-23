package com.eventbus_demo.publisher_subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度中心
 */
public class Dispatcher {

    /**
     * 维护订阅者集合
     */
    private List<Subscriber> subscribers;

    /**
     * 单例模式实例对象
     */
    private static Dispatcher instance;
    private Dispatcher() {
        this.subscribers = new ArrayList<>();
    }
    public static Dispatcher getInstance() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    /**
     * 注册订阅者
     * @param subscriber
     */
    public void register(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * 取消订阅者
     * @param subscriber
     */
    public void unregister(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * 发送消息
     *      将接收到的事件发送给订阅者
     * @param msg
     */
    public void post(String msg) {
        for (int i = 0; i < subscribers.size(); i++) {
            subscribers.get(i).onEvent(msg);
        }
    }
}