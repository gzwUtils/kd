package com.gzw.kd.learn.code;

import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 * @description： volatile
 * @since：2023/4/18 18:16
 */

@SuppressWarnings("all")
public class TestVolatile {

    /**
     * 1、可见性
     *  操作共享变量时 内存模型会把该线程对应的本地内存中的变量强制刷新到主内存中去；
     *
     *  这个写会操作会导致其他线程中的缓存无效。
     *
     *
     * 2、有序性  禁止指令重排序  通过内存屏障
     */

    volatile boolean status = true;

    /**
     * 若状态为true，则running。
     */
    public void run(){
        System.out.println("run start ......");
        while (status){
        }
        System.out.println("run end...");
    }


    public static void main(String[] args) throws InterruptedException {
        TestVolatile testVolatile = new TestVolatile();
        new Thread(testVolatile::run,"t1").start();
        TimeUnit.SECONDS.sleep(1);
        testVolatile.status = false;

    }
}
