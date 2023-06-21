package com.gzw.kd.learn.model.callback;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2023/6/21 14:47
 */
@Slf4j
public class Server {

    public void getClientMsg(KdCallBack kdCallBack , String msg) {
        log.info("server receive msg : {}",msg);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            log.error("server error {}",e.getMessage(),e);
        }
        String status = "200";
        log.info("server send status : {}",status);
        kdCallBack.process(status);
    }
}
