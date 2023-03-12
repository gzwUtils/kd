package com.gzw.kd.learn.code;
import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 * @description：
 * @since：2022/12/29 17:32
 */

@SuppressWarnings("all")
public class HelloVolitle {

   volatile boolean running = true;

    void m(){
        System.out.println("starting");
        while (running){

        }
        System.out.println("end");
    }

    public static void main(String[] args) {
        HelloVolitle volitle = new HelloVolitle();
        new Thread(volitle::m,"t1").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        volitle.running =false;
    }

}
