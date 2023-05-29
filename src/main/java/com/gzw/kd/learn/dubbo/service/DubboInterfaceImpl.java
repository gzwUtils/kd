package com.gzw.kd.learn.dubbo.service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 11:05
 */

@SuppressWarnings("all")
public class DubboInterfaceImpl implements DubboInterface {
    @Override
    public void say() {
        System.out.println("你好");
    }
}
