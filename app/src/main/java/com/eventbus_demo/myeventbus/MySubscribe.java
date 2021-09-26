package com.eventbus_demo.myeventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)     // 该注解保留到运行时
@Target(ElementType.METHOD)             // 该注解作用于方法
public @interface MySubscribe {
    /**
     * 注解属性, 设置线程模式, 默认是 POSTING,
     *      即在发布线程调用订阅方法
     * @return
     */
    MyThreadMode threadMode() default MyThreadMode.POSTING;
}