package com.gzw.kd.learn.model.single;

/**
 * 项目名称：spring-demo
 * 类 名 称：SingletonDemo1
 * 类 描 述：单例模式
 * 创建时间：2021/5/31 11:18 下午
 *
 * @author gzw
 */
public class SingletonDemo1 {

    public static final SingletonDemo1 INSTANCE=new SingletonDemo1();

    private SingletonDemo1(){

    }
}
