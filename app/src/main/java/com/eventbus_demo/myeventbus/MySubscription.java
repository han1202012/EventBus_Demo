package com.eventbus_demo.myeventbus;

/**
 * 封装 订阅者对象 与 订阅方法
 */
public class MySubscription {
    /**
     * 订阅者对象
     */
    private final Object subscriber;
    /**
     * 订阅方法
     */
    private final MySubscriberMethod subscriberMethod;

    public MySubscription(Object subscriber, MySubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public MySubscriberMethod getSubscriberMethod() {
        return subscriberMethod;
    }
}