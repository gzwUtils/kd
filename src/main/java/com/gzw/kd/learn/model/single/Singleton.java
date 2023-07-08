package com.gzw.kd.learn.model.single;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目名称：spring-demo
 * 类 名 称：SingletonDemo1
 * 类 描 述：单例模式-饿汉式
 * 创建时间：2021/5/31 11:18 下午
 *
 * @author gzw
 */
@Slf4j
@SuppressWarnings("unused")
public class Singleton {

    public static final Singleton INSTANCE=new Singleton();

    private Singleton(){

    }

    public static Singleton getInstance(){
        return INSTANCE;
    }

    public void log(){
        log.info("singleton.........");
    }
}
