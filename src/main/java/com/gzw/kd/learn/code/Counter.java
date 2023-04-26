package com.gzw.kd.learn.code;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gzw
 * @description：
 * @since：2023/4/18 18:38
 */

@SuppressWarnings("all")
public class Counter {

    /**
     *volatile 不能保证原子性
     */

   // public static volatile int num = 0;

    public static AtomicInteger num = new AtomicInteger(0);


    static CountDownLatch countDownLatch = new CountDownLatch(30);

    public static void main(String[] args) throws InterruptedException {

        for(int i=0;i<30;i++){
          new Thread(()->{
              for(int j=0;j<10000;j++){
                  num.incrementAndGet();
              }
              countDownLatch.countDown();
          }).start();
        }

        countDownLatch.await();
        System.out.println(num);
    }
}
