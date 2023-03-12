package com.gzw.kd.learn.code;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author gzw
 * @description：
 * @since：2023/2/7 23:59
 */
@SuppressWarnings("all")
public class TestLockSupport {

    public static void main(String[] args) {
        Thread t=new Thread(()->{
            for (int i=0;i<10;i++){
                System.out.println(i);
                if(i == 5){
                    LockSupport.park();
                }
                if(i == 8){
                    LockSupport.park();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        t.start();
        LockSupport.unpark(t);
    }
}
