package com.gzw.kd.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class MyLinkedBlockQueue<E> implements Serializable {

    private static final long serialVersionUID = -817911632652898426L;

    /*** 队列容器*/
    private List<E> list = new LinkedList<E>();
    /*** 可重入锁*/
    private Lock lock  = new ReentrantLock();
    /**读条件,队列为空时,用于阻塞读线程,唤醒写线程*/
    private Condition emptyLock ;
    /**写条件,队列已满时,用于阻塞写线程,唤醒读线程*/
    private Condition fullLick ;
    /**默认队列大小*/
    private final static int DEFAULT_QUEUQE_SIZE = 50;

    /**队列大小*/
    private int size;

    public MyLinkedBlockQueue() {
        this(DEFAULT_QUEUQE_SIZE);
    }


    public void  setSize(int size){
        this.size = size;
    }

    public MyLinkedBlockQueue(int size) {
        this(size,false);
    }

    public MyLinkedBlockQueue(boolean fair) {
        this(DEFAULT_QUEUQE_SIZE,fair);
    }

    public MyLinkedBlockQueue(int size,boolean fair) {

        if (size <= 0){
            throw new NullPointerException();
        }
        this.list = new LinkedList<E>();
        this.lock = new ReentrantLock(fair);
        this.size = size ;
        this.emptyLock = lock.newCondition();
        this.fullLick = lock.newCondition();
    }

    public Boolean offer(E num) {
        try {
            lock.lock();
            if (list.size() == size) {
                log.info("队列已满.......");
                return false;
            }
            list.add(num);
            emptyLock.signal();
        } catch (Exception e) {
            emptyLock.signal();
            log.error("put queue error ", e);
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }


    public void put(E num) {
        try {
            lock.lock();
            while (list.size() == size) {
                log.info(".....队列已满 等待消费数据.......");
                fullLick.await();
            }
            list.add(num);
            emptyLock.signal();
        } catch (Exception e) {
            emptyLock.signal();
            log.error("put queue error ",e);
        }finally {
            lock.unlock();
        }
    }


    public E take() {
        try {
            lock.lock();
            while (list.size() == 0) {
                log.info("队列已空 等待写入数据");
                emptyLock.await();
            }
            E remove = list.remove(0);
            fullLick.signal();
            return remove;
        } catch (Exception e) {
            fullLick.signal();
            log.error("take queue error ", e);
        } finally {
            lock.unlock();
        }
        return null;
    }
}
