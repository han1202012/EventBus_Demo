package com.eventbus_demo.publisher_subscriber;

/**
 * 订阅者
 */
public interface Subscriber {
    /**
     * 处理事件
     * @param msg 接收到的事件
     */
    void onEvent(String msg);
}