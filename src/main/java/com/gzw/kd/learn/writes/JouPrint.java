package com.gzw.kd.learn.writes;

public class JouPrint {
    private static Object lock = new Object();
    private static int number = 1;
    private static final int MAX_NUMBER = 100;


    public static void main(String[] args) {
        new ThreadA().start();
        new ThreadB().start();
    }
    static class ThreadA extends Thread {
        @Override
        public void run() {
            while (number <= MAX_NUMBER) {
                synchronized (lock) {
                    if (number % 2 != 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(number);
                        number++;
                        lock.notify();
                    }
                }
            }
        }
    }

    static class ThreadB extends Thread {
        @Override
        public void run() {
            while (number <= MAX_NUMBER) {
                synchronized (lock) {
                    if (number % 2 == 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(number);
                        number++;
                        lock.notify();
                    }
                }
            }
        }
    }
}
