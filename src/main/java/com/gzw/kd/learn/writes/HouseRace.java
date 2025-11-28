package com.gzw.kd.learn.writes;

import com.alipay.api.domain.House;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class HouseRace {

    private static List<String> result = Collections.synchronizedList(new ArrayList<>());

    private static CountDownLatch countDownLatch  = new CountDownLatch(1);


    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    });


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new House("ouse" + i, i)).start();
        }

        System.out.println("all house ready start now ");

        countDownLatch.countDown();

    }


static class House implements Runnable {
    private String name;
    private int time;
    public House(String name, int time) {
        this.name = name;
        this.time = time;
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();

           long l = (long) (Math.random() * 10000);
           Thread.sleep(l);
           result.add(name + ": " + l);
           cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
}
