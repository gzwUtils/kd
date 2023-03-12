package com.gzw.kd.learn.model.single;

/**
 * 项目名称：spring-demo
 * 类 名 称：SingletonDemo
 * 类 描 述：单例模式
 * 创建时间：2021/5/31 11:13 下午
 *
 * @author gzw
 */
public class SingletonDemo {
    /**
     * 分配内存 构造对象 建立关联 （指令重排）
     * volatile 可见性  有序性（禁止指令重排，内存屏障（特殊的一条指令  jvm ll ss ls  sl ））
     * happens-before 原则
     * volatile  hotspot （lock addl ）汇编。
     * 将当前处理器对应缓存的内存刷新到内存。
     * synchronized  reentrantlock  可重入锁
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

}
