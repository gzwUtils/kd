package com.gzw.kd.learn.model.single;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 静态内部类 -延迟加载
 * @since：2023/7/7 10:17
 */
@Slf4j
@SuppressWarnings("unused")
public class Single {


    private Single() {}

    private static class SingleHolder{
        private static final Single INSTANCE = new Single();
    }

    public static Single getInstance(){
        return SingleHolder.INSTANCE;
    }

    public void log(){
        log.info("single.........");
    }
}
