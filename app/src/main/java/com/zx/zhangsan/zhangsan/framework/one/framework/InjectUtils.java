package com.zx.zhangsan.zhangsan.framework.one.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者: Dream on 2017/8/26 22:29
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public class InjectUtils {

    public static void inject(Object obj) {
        injectLayout(obj);
        Map<Integer, Object> viewMap = injectView(obj);
        injectEvent(obj, viewMap);
    }

    public static void injectLayout(Object obj) {
        // 获取Activity的ContentView的注解
        Class<?> handlerType = obj.getClass();
        try {
            //获取类对象身上的注解
            ContentView contentView = handlerType.getAnnotation(ContentView.class);
            if (contentView != null) {
                //获取布局ID
                int viewId = contentView.value();
                if (viewId > 0) {
                    Method setContentViewMethod = handlerType.getMethod(
                            "setContentView", int.class);
                    setContentViewMethod.invoke(obj, viewId);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static Map<Integer, Object> injectView(Object handler) {
        Map<Integer, Object> viewMap = new HashMap<Integer, Object>();
        //获取类对象
        Class<?> handlerType = handler.getClass();
        //获取对象属性列表
        Field[] fields = handlerType.getDeclaredFields();
        //判定是否存在属性
        if (fields != null && fields.length > 0) {
            //遍历属性
            for (Field field : fields) {

                //判断属性修饰符
                Class<?> fieldType = field.getType();
                if (
                /* 不注入静态字段 */Modifier.isStatic(field.getModifiers()) ||
				/* 不注入final字段 */Modifier.isFinal(field.getModifiers()) ||
				/* 不注入基本类型字段(int、double、float、char、boolean等等...) */fieldType.isPrimitive() ||
				/* 不注入数组类型字段 */fieldType.isArray()) {
                    continue;
                }

                //获取属性注解
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        //获取ViewID
                        int viewId = viewInject.value();
                        //获取findViewById方法对象
                        Method findViewByIdMethod = handlerType.getMethod(
                                "findViewById", int.class);
                        //执行findViewById方法，获取对象
                        Object view = findViewByIdMethod.invoke(handler,viewId);
                        if (view != null) {
                            //修改访问权限(private)
                            //setAccessible:将属性修饰符修改为public
                            field.setAccessible(true);
                            //赋值
                            field.set(handler, view);

                            viewMap.put(viewId, view);
                        } else {
                            throw new RuntimeException(
                                    "Invalid @ViewInject for "
                                            + handlerType.getSimpleName() + "."
                                            + field.getName());
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return viewMap;
    }


    public static void injectEvent(Object obj, Map<Integer, Object> viewMap){
        //获取类对象
        Class<?> handlerType = obj.getClass();
        //获取对象方法->activity
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            //遍历方法
            for (Method method : methods) {
                //获取方法注解
                Annotation[] annotations = method.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    //遍历注解目的：为了获取我们想要的注解对象
                    for (Annotation annotation : annotations) {
                        //获取注解身上注解
                        //获取注解类型
                        Class<?> annType = annotation.annotationType();
                        if (annType.getAnnotation(EventBase.class) != null) {
                            method.setAccessible(true);
                            try {
                                //获取注解value方法
                                Method valueMethod = annType.getDeclaredMethod("value");
                                //获取OnClick、OnLongClick等等....注解身上的value方法
                                //values说白了就是控件的id数组
                                int[] values = (int[]) valueMethod.invoke(annotation);

                                //遍历id数组
                                for (int i = 0; i < values.length; i++) {
                                    int viewId = values[i];
                                    Object view = viewMap.get(viewId);
                                    //对事件进行动态代理
                                    EventListenerManager.addEventMethod(annotation, obj, method, view);

                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}
