package com.zx.zhangsan.zhangsan.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//事件三要素(事件源、回调处理、监听器)

//注意
//Target:作用目标->注解
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {
    //监听器
    Class<?> listenerType();

    //事件源
    String listenerSetter();

    //回调处理
    String methodName();
}
