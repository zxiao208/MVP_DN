package com.zx.zhangsan.zhangsan.framework.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = View.OnClickListener.class,listenerSetter = "setOnClickListener",methodName = "onClick")
public @interface OnClick {
    int[] value();
//    String TAG() default "默认值";  //回调事件中随变打印一句话
}
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//@EventBase(listenerType = View.OnClickListener.class, listenerSetter = "setOnClickListener", methodName = "onClick")
//public @interface OnClick {
//    //viewID
//    int[] value();
//}