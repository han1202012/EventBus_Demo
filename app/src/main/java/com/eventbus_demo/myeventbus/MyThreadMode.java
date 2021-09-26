package com.eventbus_demo.myeventbus;

/**
 * 直接使用 EventBus 中的现成模式
 */
public enum MyThreadMode {
    POSTING,
    MAIN,
    MAIN_ORDERED,
    BACKGROUND,
    ASYNC
}