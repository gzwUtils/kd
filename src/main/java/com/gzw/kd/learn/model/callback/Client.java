package com.gzw.kd.learn.model.callback;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2023/6/21 14:46
 */
@SuppressWarnings("all")
@Slf4j
public class Client implements KdCallBack{

    private final Server server;

    public Client(Server server){
        this.server = server ;
    }

    public void  sendMsg(String msg){
        log.info("client send msg {}",msg);
        new Thread(()->{
            server.getClientMsg(this,msg);
        },"kd").start();
    }

    @Override
    public void process(String status) {
        log.info("client receive status {}",status);
    }
}
