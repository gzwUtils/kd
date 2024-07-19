package com.gzw.kd.learn.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gaozhiwei
 */
public class SequentialPrinterWithLock {

    private static final Lock LOCK = new ReentrantLock();

    private static final Condition JS = LOCK.newCondition();

    private static final Condition OS = LOCK.newCondition();


    private static  int count = 1;

    private static final Integer MAX = 100;


    private static  final Logger logger = LoggerFactory.getLogger(SequentialPrinterWithLock.class);


    public static void main(String[] args) {
        Thread jsthread = new Thread(new JsPrinter());

        Thread thread = new Thread(new OSPrinter());

        jsthread.start();
        thread.start();
    }



    static class JsPrinter implements Runnable{

        @Override
        public void run() {

            while (count<MAX){
                LOCK.lock();
                try {
                    if(count % 2 == 0){
                        JS.await();
                    }
                    System.out.println(count);
                    count++;
                    OS.signal();
                }catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }

    static class OSPrinter implements Runnable{

        @Override
        public void run() {

            while (count<MAX){
                LOCK.lock();
                try {
                    if(count % 2 == 1){
                        OS.await();
                    }
                    System.out.println(count);
                    count++;
                    JS.signal();
                }catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }


}
