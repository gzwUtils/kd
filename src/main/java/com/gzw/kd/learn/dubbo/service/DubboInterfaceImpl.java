package com.gzw.kd.learn.dubbo.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 11:05
 */
@Slf4j
@SuppressWarnings("all")
public class DubboInterfaceImpl implements DubboInterface {
    @Override
    public void say() {
        log.warn("dubbo-hello..................");
    }
}
