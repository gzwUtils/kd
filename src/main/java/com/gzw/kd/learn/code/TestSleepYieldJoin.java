package com.gzw.kd.learn.code;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2023/4/18 17:15
 */

@SuppressWarnings("all")
@Slf4j
public class TestSleepYieldJoin {


    public static void main(String[] args) {
       testSleep();
       testYield();
       testJoin();
    }


    static void testSleep(){
        new Thread(()->{
            for(int i=0;i<100;i++){
                System.out.println("A"+i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("error......",e);
                }
            }
        }).start();
    }

    /**
     * yield 就是我让出一下cpu 后面能不能抢到我不管
     */
    static void testYield(){
        new Thread(()->{
            for(int i = 0 ; i <100 ; i++){
                System.out.println("A"+i);
                if(i%10 == 0){
                    Thread.yield();
                }
            }
        }).start();

        new Thread(()->{
            for(int i = 0 ; i <100 ; i++){
                System.out.println("------------B"+i);
                if(i%10 == 0){
                    Thread.yield();
                }
            }
        }).start();
    }



    static void testJoin(){
        Thread t1 =   new Thread(()->{
            for(int i = 0 ; i <100 ; i++){
                System.out.println("A"+i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("error join.......",e);
                }
            }
        });

        Thread t2 =  new Thread(()->{
            try {
                t1.join();
            }catch (Exception e){
                log.error("error .......",e);
            }
            for(int i = 0 ; i<100;i++){
                System.out.println("A---"+i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("error join.......",e);
                }
            }
        });
        t1.start();
        t2.start();
    }
}
