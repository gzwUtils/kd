package com.gzw.kd.learn.dubbo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 10:55
 */

@SuppressWarnings("all")
public class ProxyFactory implements InvocationHandler {

    private final Class interfaceClass;

    public ProxyFactory(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public <T> T getProxyObject(){
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{interfaceClass},this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method);
        return null;
    }
}
