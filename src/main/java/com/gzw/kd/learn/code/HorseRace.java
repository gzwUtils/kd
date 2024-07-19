package com.gzw.kd.learn.code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author gaozhiwei
 */
public class HorseRace {

    //用于存储赛马成绩

    private static final List<String> results = Collections.synchronizedList(new ArrayList<>());

    //用于控制所有马开跑

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private static  final Logger logger = LoggerFactory.getLogger(HorseRace.class);

    //用于宣布成绩

    private static final CyclicBarrier CYCLIC_BARRIER = new CyclicBarrier(10, new Runnable() {
        @Override
        public void run() {
            System.out.println("开始宣布成绩............");
            for (String result : results) {
                System.out.println(result);
            }
        }
    });


    public static void main(String[] args) {
        for (int i = 0; i <10 ; i++) {
            new Thread(new Horse("Horse"+ i)).start();
            System.out.println("Horse"+ i + "准备就绪");
        }
        try {
            System.out.println("所有马就绪 开始跑");
            COUNT_DOWN_LATCH.countDown();
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }



    static class Horse implements Runnable {

        private final String name;

        public Horse (String name) {
            this.name = name;
        }

        @Override
        public void run() {

            try {
                //马就绪 等待开跑
                COUNT_DOWN_LATCH.await();

                //马开始跑

                long l = (long) (Math.random() * 1000);

                Thread.sleep(l);

                results.add(name+ " finished in "+ l + "ms");

                System.out.println(name +" 完成跑");

                //等待所有马到达终点
                CYCLIC_BARRIER.await();
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

}
