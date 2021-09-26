package com.eventbus_demo.myeventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyEventBus {

    /**
     * 方法缓存
     *      Key - 订阅方法事件参数类型
     *      Value - 订阅者对象 + 订阅方法 封装类 MySubscriberMethod 的集合
     * 取名与 EventBus 一致
     */
    private static final Map<Class<?>, List<MySubscriberMethod>> METHOD_CACHE = new HashMap<>();

    /**
     * 解除注册时使用
     *      Key - 订阅者对象
     *      Value - 订阅者对象中所有的订阅方法的事件参数类型集合
     *
     * 根据该订阅者对象 , 查找所有订阅方法的事件参数类型 ,  然后再到  METHOD_CACHE 中 ,
     *      根据事件参数类型 , 查找对应的 MySubscriberMethod 集合
     *      MySubscriberMethod 中封装 订阅者对象 + 订阅方法
     *
     */
    private final Map<Object, List<Class<?>>> typesBySubscriber;

    /**
     * Key - 订阅者方法事件参数类型
     * Value - 封装 订阅者对象 与 订阅方法 的 MySubscription 集合
     * 在构造函数中初始化
     * CopyOnWriteArrayList 在写入数据时会拷贝一个副本 ,
     *      写完之后 , 将引用指向新的副本 ,
     *      该集合的线程安全级别很高
     */
    private final Map<Class<?>, CopyOnWriteArrayList<MySubscription>> subscriptionsByEventType;

    /**
     * 全局单例
     */
    private static MyEventBus instance;
    private MyEventBus() {
        subscriptionsByEventType = new HashMap<>();
        typesBySubscriber = new HashMap<>();
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
     * @param subscriberClass   订阅者对象的类型
     * @return
     */
    private List<MySubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        // 获取 Class<?> clazz 参数类型对应的 订阅者封装类
        List<MySubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);



        if (subscriberMethods == null) {
            // 说明是首次获取 , 初始化 METHOD_CACHE 缓存
            // 反射获取 Class<?> subscriberClass 中的所有订阅方法
            subscriberMethods = findByReflection(subscriberClass);

            if (! subscriberMethods.isEmpty()) {
                //METHOD_CACHE.put(subscriberClass, subscriberMethods);
            }

            
        } else {
            // 如果当前不是第一次获取, 则直接返回从 METHOD_CACHE 缓存中获取的 订阅者封装类 集合
            return subscriberMethods;
        }

        // 该分支走不到
        return null;
    }

    /**
     * 通过反射获取 Class<?> subscriberClass 订阅方法
     * @param subscriberClass 订阅类
     * @return
     */
    private List<MySubscriberMethod> findByReflection(Class<?> subscriberClass) {
        // 要返回的 MySubscriberMethod 集合
        List<MySubscriberMethod> subscriberMethods = new ArrayList<>();

        // 通过反射获取所有带 @MySubscribe 注解的方法
        Method[] methods = subscriberClass.getMethods();

        // 遍历所有的方法 , 查找注解
        for (Method method : methods) {
            // 获取方法修饰符
            int modifiers = method.getModifiers();
            // 获取方法参数
            Class<?>[] params = method.getParameterTypes();
            // 确保修饰符必须是 public , 参数长度必须是 1
            if (modifiers == Modifier.PUBLIC && params.length == 1) {
                // 获取 MySubscribe 注解
                MySubscribe annotation = method.getAnnotation(MySubscribe.class);
                // 获取注解不为空
                if (annotation != null) {
                    // 获取线程模式
                    MyThreadMode threadMode = annotation.threadMode();
                    // 此时已经完全确定该方法是一个订阅方法 , 直接进行封装
                    MySubscriberMethod subscriberMethod = new MySubscriberMethod(
                            method,         // 方法对象
                            threadMode,     // 线程模式
                            params[0]       // 事件参数
                    );
                }
            }
        }

        return null;
    }

}