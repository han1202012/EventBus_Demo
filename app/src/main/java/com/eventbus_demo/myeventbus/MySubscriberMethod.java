package com.eventbus_demo.myeventbus;

import java.lang.reflect.Method;

/**
 * 该类中用于保存订阅方法相关信息
 */
public class MySubscriberMethod {
    /**
     * 订阅方法
     */
    private final Method method;
    /**
     * 订阅方法的线程模式
     */
    private final MyThreadMode threadMode;
    /**
     * 订阅方法接收的事件类型
     */
    private final Class<?> eventType;

    public MySubscriberMethod(Method method, MyThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public MyThreadMode getThreadMode() {
        return threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }
}
