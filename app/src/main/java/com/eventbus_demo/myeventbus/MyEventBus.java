package com.eventbus_demo.myeventbus;

import java.util.List;

public class MyEventBus {

    /**
     * 全局单例
     */
    private static MyEventBus instance;
    private MyEventBus() {

    }
    public static MyEventBus getInstance() {
        if (instance == null) {
            instance = new MyEventBus();
        }
        return instance;
    }

    /**
     * 注册订阅者
     * @param subscriber
     */
    public void register(Object subscriber) {
        // 获取订阅者所述类
        Class<?> clazz = subscriber.getClass();
        // 查找订阅方法
        List<MySubscriberMethod> subscriberMethods = findSubscriberMethods(clazz);

        //


    }

    /**
     * 根据订阅方法的事件参数查找订阅方法
     * @param clazz
     * @return
     */
    private List<MySubscriberMethod> findSubscriberMethods(Class<?> clazz) {
        return null;
    }

}