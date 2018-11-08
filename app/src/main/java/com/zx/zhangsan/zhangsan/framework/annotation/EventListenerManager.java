/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zx.zhangsan.zhangsan.framework.annotation;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class EventListenerManager {

    private static DynamicHandler dynamicHandler;

    private EventListenerManager() {
    }

    public static void addEventMethod(Annotation eventAnnotation, Object handler, Method method, Object view) {
        try {
            if (view != null) {
                EventBase eventBase = eventAnnotation.annotationType()
                        .getAnnotation(EventBase.class);
                // 监听类型:OnClickListener、OnTouchListener、OnLongClickListener等等......
                Class<?> listenerType = eventBase.listenerType();
                // 事件源(你要给那个View绑定监听，而且该监听对应的方法)
                // View.setOnClickListener() View.setOnTouchListener
                // View.setOnLongClickListener
                String listenerSetter = eventBase.listenerSetter();
                // 监听方法: onClick方法、onTouch、onLongClick方法
                String methodName = eventBase.methodName();

                // 从缓存中获取
                // 提高了性能，节约内存

                Object listener = null;

                // 第一次添加监听
                dynamicHandler = new DynamicHandler(handler);
                dynamicHandler.addMethod(methodName, method);

                // listener：代理对象
                listener = Proxy.newProxyInstance(
                        listenerType.getClassLoader(),
                        new Class<?>[]{listenerType}, dynamicHandler);

                // 绑定监听
                Method setEventListenerMethod = view.getClass().getMethod(
                        listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, listener);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void addEventMethod_3_0(
            Event event, Object handler, Method method, Object view) {
        try {
            if (view != null && event != null) {
                // 监听类型:OnClickListener、OnTouchListener、OnLongClickListener等等......
                Class<?> listenerType = event.type();
                // 事件源(你要给那个View绑定监听，而且该监听对应的方法)
                // View.setOnClickListener() View.setOnTouchListener
                // View.setOnLongClickListener
                String listenerSetter = event.setter();
                // 监听方法: onClick方法、onTouch、onLongClick方法
                String methodName = event.method();

                // 从缓存中获取
                // 提高了性能，节约内存

                Object proxy = null;

                // 第一次添加监听
                dynamicHandler = new DynamicHandler(handler);
                dynamicHandler.addMethod(methodName, method);

                // proxy：代理对象
                proxy = Proxy.newProxyInstance(
                        listenerType.getClassLoader(),
                        new Class<?>[]{listenerType}, dynamicHandler);

                // 绑定监听
                Method setEventListenerMethod = view.getClass().getMethod(
                        listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, proxy);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    // WeakReference？为什么？
    // 第一点：及时清理内存
    // 第二点：Activity很有可能会被意外释放(意外关闭，而这个时候你刚好执行代码到了控件的加载)
    // 添加软引用目的：为了防止对象意外被释放关闭而产生异常（典型：空指针异常）
    public static class DynamicHandler implements InvocationHandler {
        private WeakReference<Object> handlerRef;
        private final HashMap<String, Method> methodMap = new HashMap<String, Method>(
                1);

        // 目标对象: Activity、Fragment
        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        public void addMethod(String name, Method method) {
            methodMap.put(name, method);
        }

        public Object getHandler() {
            return handlerRef.get();
        }

        public void setHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            Object handler = handlerRef.get();
            if (handler != null) {
                String methodName = method.getName();
                method = methodMap.get(methodName);
                // 为什么做判断？
                // 目的：确定代理要代理的方法
                if (method != null) {
                    return method.invoke(handler, args);
                }
            }
            return null;
        }
    }
}
