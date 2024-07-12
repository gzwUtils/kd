package com.gzw.kd.learn.model.single;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目名称：spring-demo
 * 类 名 称：SingletonDemo
 * 类 描 述：单例模式- 懒汉式
 * 创建时间：2021/5/31 11:13 下午
 *
 * @author gzw
 */

@Slf4j
@SuppressWarnings("unused")
public class SingletonDemo {
    /**
     * 构造函数需要是 private 访问权限的，这样才能避免外部通过 new 创建实例;
     * 考虑对象创建时的线程安全问题;
     * 考虑是否支持延迟加载;
     * 考虑 getInstance() 性能是否高(是否加锁)
     */

    private static volatile SingletonDemo singletonDemo = null;

    private SingletonDemo() {

    }

    public static SingletonDemo getSingletonDemo() {
        if (singletonDemo == null) {
            synchronized (SingletonDemo.class) {
                if (singletonDemo == null) {
                    singletonDemo = new SingletonDemo();
                }
            }
        }
        return singletonDemo;
    }


    public void log(){
        log.info("singletonDemo.........");
    }

}
