package com.gzw.kd.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec
 */


@Slf4j
@SuppressWarnings("all")
public class SynchronizedBlockingQueue<E>{

    /** 读条件 队列为空时 阻塞读线程  唤醒写线程*/
    private final Object notEmpty;
    /** 写条件 队列满时 阻塞写线程 唤醒读线程*/
    private final Object notFull;
    /** 队列容器*/
    private Object[] items;
    /** 出队索引*/
    private int takeIndex;
    /** 入队索引*/
    private int putIndex;

    /** 队列中的元素数量*/
    private int count;


    /**
     * 默认 非公平
     * @param capacity 队列大小
     */
    public SynchronizedBlockingQueue(int capacity) {
        this(capacity,false);
    }

    /**
     * 容器初始化  可设置重入锁类型
     * @param capacity 队列大小
     * @param fair 是否公平
     */

    public SynchronizedBlockingQueue(int capacity,boolean fair) {

        if (capacity <= 0){
            throw new NullPointerException();
        }
        /**
         * 初始化容器
         */
        this.items = new Object[capacity];

        /**
         * 非空得时候读
         */
        notEmpty = new Object();
        /**
         * 非满得时候写
         */
        notFull = new Object();

    }


    public boolean offer(E obj){
        if(obj == null){
            throw new NullPointerException();
        }

        /**
         * 队列已满
         */
        synchronized (notFull){
            try {
                while (count == items.length){
                    log.info("队列已满,等待消费数据,size:{},items.length:{}",count,items.length);
                    //阻塞写线程
                    notFull.wait();
                }
            }catch (Exception e){
                log.error("syn offer error ",e);
            }
        }

        /**
         * 队列未满
         */
        synchronized (notEmpty) {
            items[putIndex] = obj;
            //写入元素后入队索引等于 容器大小,表明容器已满,将入队索引重置为0,从头开始(++putIndex:先加1在使用)
            if (++putIndex == items.length) {
                putIndex = 0;
            }

            count++;
            //唤醒读线程
            notEmpty.notify();
            return true;
        }
    }

    public E take(){

        /**
         * 队列为空
         */
        synchronized (notEmpty){
            try {
                while (count == 0) {
                    log.info("队列为空,等待生产数据,size:{},items.length:{}", count, items.length);
                    //阻塞读线程
                    notEmpty.wait();
                }
            } catch (InterruptedException e) {
                log.error("syn take error",e);
            }
        }

        /**
         * 队列非空
         */

        synchronized (notFull){
            Object item = items[takeIndex];
            if(++takeIndex == items.length){
                takeIndex = 0 ;
            }

            count--;

            notFull.notify();

            return (E) item;
        }
    }
}
