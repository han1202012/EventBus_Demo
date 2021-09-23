package com.eventbus_demo.publisher_subscriber;

/**
 * 发布者
 */
public class Publisher {
    /**
     * 发布消息
     * @param msg 要发布的消息
     */
    public void post(String msg) {
        Dispatcher.getInstance().post(msg);
    }
}