package com.zx.zhangsan.zhangsan.framework.one.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Target:作用目标->作用在属性身上(ElementType.FIELD)
@Target(ElementType.FIELD)
//Retention:生命周期->运行时注解(RetentionPolicy.RUNTIME)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {

    //View的ID
    int value();
}
