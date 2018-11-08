package com.zx.zhangsan.zhangsan.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//类注解
//Target:作用目标->作用在类身上(ElementType.TYPE)
@Target(ElementType.TYPE)
//Retention:生命周期->运行时注解(RetentionPolicy.RUNTIME)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {
    //布局ID
    int value();
}
