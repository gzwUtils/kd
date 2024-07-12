package com.gzw.kd.service;

/**
 * @author gzw
 * @description： 消息监听
 * @since：2023/6/16 18:06
 */
public interface ObserverListener {

    /**
     * 发布消息
     * @param message msg
     */
    void  push(String message);
}
