package com.zx.zhangsan.zhangsan.framework.annotation;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InjectUtils2 {
    public static void inject(Object obj) {
        injectLayout(obj);
        Map<Integer, Object> viewMap = injectView(obj);
//        injectEvent(obj, viewMap);
        injectEvent_3_0(obj,viewMap);
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


    //
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


    //3.0版本->实现
    public static void injectEvent_3_0(Object handler, Map<Integer, Object> viewMap){
        //获取类对象
        Class<?> handlerType = handler.getClass();
        //获取对象方法->activity
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {

                // 注意：静态方法不允许添加控件注解,私有方法运行访问，非私有方法不允许访问
                // 在XUtils框架3.0之后，要求我们的方法必须是私有方法(注意：public不行)
                // 希望该方法配置了注解，不希望子类继承，只有当前类可以享受
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                // 检查当前方法是否是event注解的方法
                Event event = method.getAnnotation(Event.class);
                if (event != null) {
                    try {
                        // id参数
                        int[] values = event.value();
                        // 循环所有id，生成ViewInfo并添加代理反射
                        for (int i = 0; i < values.length; i++) {
                            int valueId = values[i];
                            if (valueId > 0) {
                                Object view = viewMap.get(valueId);
                                // ViewInfo info = new ViewInfo();
                                // 不管你再怎么样，永远都会创建对象
                                method.setAccessible(true);
                                EventListenerManager.addEventMethod_3_0(event, handler, method, view);
                            }
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
