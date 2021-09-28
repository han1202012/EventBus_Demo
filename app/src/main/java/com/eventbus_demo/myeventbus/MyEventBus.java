package com.eventbus_demo.myeventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyEventBus {

    /**
     * 方法缓存
     *      Key - 订阅类类型
     *      Value - 订阅方法 MySubscriberMethod 的集合
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
     * 线程池
     */
    private final ExecutorService executorService;

    /**
     * 全局单例
     */
    private static MyEventBus instance;
    private MyEventBus() {
        subscriptionsByEventType = new HashMap<>();
        typesBySubscriber = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
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
        // 获取订阅者所属类
        Class<?> clazz = subscriber.getClass();
        // 查找订阅方法
        List<MySubscriberMethod> subscriberMethods = findSubscriberMethods(clazz);

        // 遍历所有订阅方法 , 进行订阅
        //      首先确保查找到的订阅方法不为空 , 并且个数大于等于 1 个
        if (subscriberMethods != null && !subscriberMethods.isEmpty()) {
            for (MySubscriberMethod method : subscriberMethods) {
                // 正式进行订阅
                subscribe(subscriber, method);
            }
        }
    }

    /**
     * 方法订阅
     *      将 订阅方法参数类型 和 订阅类 + 订阅方法 封装类 , 保存到
     *      Map<Class<?>, CopyOnWriteArrayList<MySubscription>> subscriptionsByEventType 集合中
     *          Key - 订阅者方法事件参数类型
     *          Value - 封装 订阅者对象 与 订阅方法 的 MySubscription 集合
     *
     * 取消注册数据准备
     *      取消注册数据存放在 Map<Object, List<Class<?>>> typesBySubscriber 集合中
     *          Key - 订阅者对象
     *          Value - 订阅者方法参数集合
     *
     * @param subscriber    订阅者对象
     * @param subscriberMethod        订阅方法
     */
    private void subscribe(Object subscriber, MySubscriberMethod subscriberMethod) {
        // 获取订阅方法接收的参数类型
        Class<?> eventType = subscriberMethod.getEventType();
        // 获取 eventType 参数类型对应的 订阅者封装类 ( 封装 订阅者对象 + 订阅方法 ) 集合
        CopyOnWriteArrayList<MySubscription> subscriptions =
                subscriptionsByEventType.get(eventType);

        // 如果获取的集合为空 , 说明 eventType 参数对应的订阅方法一个也没有注册过
        //      这里先创建一个集合 , 放到 subscriptionsByEventType 键值对中
        if (subscriptions == null) {
            // 创建集合
            subscriptions = new CopyOnWriteArrayList<>();
            // 将集合设置到 subscriptionsByEventType 键值对集合中
            subscriptionsByEventType.put(eventType, subscriptions);
        }

        // 封装 订阅者对象 + 订阅方法 对象
        MySubscription subscription = new MySubscription(subscriber, subscriberMethod);
        // 将创建的 订阅者对象 + 订阅方法 对象 添加到  CopyOnWriteArrayList 集合中
        subscriptions.add(subscription);

        // 为取消注册准备数据
        //      设置 Map<Object, List<Class<?>>> typesBySubscriber
        List<Class<?>> eventTypes = typesBySubscriber.get(subscriber);
        if (eventTypes == null) {
            // 创建新的集合, 用于存放订阅方法的参数类型
            eventTypes = new ArrayList<>();
            // 将新的集合设置到 Map<Object, List<Class<?>>> typesBySubscriber 集合中
            typesBySubscriber.put(subscriber, eventTypes);
        }
        // 将新的 订阅方法类型 放入到集合中
        eventTypes.add(eventType);
    }

    /**
     * 根据订阅方法的事件参数查找订阅方法
     * @param subscriberClass   订阅者对象的类型
     * @return
     */
    private List<MySubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        // 获取 Class<?> clazz 参数类型对应的 订阅者封装类
        List<MySubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);

        // 此处后期重构, 减少缩进

        if (subscriberMethods == null) {
            // 说明是首次获取 , 初始化 METHOD_CACHE 缓存
            // 反射获取 Class<?> subscriberClass 中的所有订阅方法
            subscriberMethods = findByReflection(subscriberClass);

            if (! subscriberMethods.isEmpty()) {
                METHOD_CACHE.put(subscriberClass, subscriberMethods);
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
                    // 加入到返回集合中
                    subscriberMethods.add(subscriberMethod);
                }
            }
        }
        return subscriberMethods;
    }

    /**
     * 接收到了 发布者 Publisher 发送给本消息中心 的 Event 消息事件对象
     *      将该事件对象转发给相应接收该类型消息的 订阅者 ( 订阅对象 + 订阅方法 )
     *      通过事件类型到
     *      Map<Class<?>, CopyOnWriteArrayList<MySubscription>> subscriptionsByEventType
     *      集合中查找相应的 订阅对象 + 订阅方法
     * @param event
     */
    public void post(Object event) {
        // 获取事件类型
        Class<?> eventType = event.getClass();
        // 获取事件类型对应的 订阅者 集合
        CopyOnWriteArrayList<MySubscription> subscriptions =
                subscriptionsByEventType.get(eventType);

        // 确保订阅者大于等于 1 个
        if (subscriptions != null && subscriptions.size() > 0) {
            // 遍历订阅者并调用订阅方法
            for (MySubscription subscription : subscriptions) {
                postSingleSubscription(subscription, event);
            }
        }
    }

    /**
     * 调用订阅方法
     * @param subscription
     * @param event
     */
    private void postSingleSubscription(MySubscription subscription, Object event) {
        // 判断当前线程是否是主线程
        //      获取 mainLooper 与 myLooper 进行比较 , 如果一致 , 说明该线程是主线程
        boolean isMainThread = false;
        // 下面的情况下 , 线程是主线程
        if (Looper.getMainLooper() == Looper.myLooper()) {
            isMainThread = true;
        }

        // 判断订阅方法的线程模式
        MyThreadMode threadMode = subscription.getSubscriberMethod().getThreadMode();

        switch (threadMode) {
            case POSTING:
                // 直接在发布线程调用订阅方法
                invokeMethod(subscription, event);
                break;
            case MAIN:
            case MAIN_ORDERED:
                // 如果发布线程是主线程, 直接调用
                if (isMainThread) {
                    invokeMethod(subscription, event);
                } else {
                    // 将订阅方法放到主线程执行
                    // 获取主线程 Looper , 并通过 Looper 创建 Handler
                    Handler handler = new Handler(Looper.getMainLooper());
                    // 在主线程中执行订阅方法
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeMethod(subscription, event);
                        }
                    });
                }
                break;
            case BACKGROUND:
            case ASYNC:
                // 如果是主线程 , 切换到子线程执行
                if (isMainThread) {
                    // 在线程池中执行方法
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            invokeMethod(subscription, event);
                        }
                    });
                } else {
                    // 如果是子线程直接执行
                    invokeMethod(subscription, event);
                }
                break;
        }
    }

    /**
     * 调用订阅者的订阅方法
     * @param subscription 订阅者对象 + 订阅方法
     * @param event 发布者传递的消息事件
     */
    private void invokeMethod(MySubscription subscription, Object event) {
        try {
            // 通过反射调用订阅方法
            subscription.getSubscriberMethod().getMethod().invoke(
                    subscription.getSubscriber(),   // 订阅者对象
                    event                           // 事件参数类型
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}