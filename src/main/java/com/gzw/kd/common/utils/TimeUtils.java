package com.gzw.kd.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/14
 * @dec
 *
 * 守护线程 会自动关闭
 */

@Slf4j
public class TimeUtils {

    private static volatile long currentTimeMillis;

    static {
        currentTimeMillis =  System.currentTimeMillis();
       Thread daemon=  new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentTimeMillis =  System.currentTimeMillis();
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
       daemon.setDaemon(true);
       daemon.setName("api-time-thread");
       daemon.start();
    }


    public static long currentTimeMillis(){
        return currentTimeMillis;
    }
}
