package com.gzw.kd.learn.writes;

public class PrinterWaitNotify {
    private static Object lock = new Object();
    private static boolean printNumber = true;


    static class PrintNumber implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 1; i <= 3; i++){
                    while (!printNumber) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(i);
                    printNumber = false;
                    lock.notify();
                }
            }
        }
    }


    static class PrintChar implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                for (char  c = 'A'; c <= 'C'; c++){
                    while (printNumber) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(c);
                    printNumber = true;
                    lock.notify();
                }
            }
        }
    }


    public static void main(String[] args) {

        new Thread(new PrintNumber()).start();
        new Thread(new PrintChar()).start();

    }
}
