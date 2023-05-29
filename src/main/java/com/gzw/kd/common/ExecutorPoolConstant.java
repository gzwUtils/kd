package com.gzw.kd.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 * @description： 线程池参数
 * @since：2023/5/19 14:16
 */

@SuppressWarnings("all")
public class ExecutorPoolConstant {


    /**
     * 线程名称前缀
     */
    public static final String EXECUTOR_NAME_PREFIX = "gzw-kd-";

    /**
     * 线程中的任务在接收到应用关闭信号量后最多等待多久就强制终止，其实就是给剩余任务预留的时间， 到时间后线程池必须销毁
     */

    public static final int AWAIT_TERMINATION = 60;

    /**
     * awaitTermination的单位
     */
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 容量为0
     */
    public static final SynchronousQueue  SYNCHRONOUS_QUEUE= new  SynchronousQueue<>();


    /** ----------------------业务  任务线程池-------------------------------*/

    /**
     * small
     */
    public static final Integer SINGLE_CORE_POOL_SIZE = 1;
    public static final Integer SINGLE_MAX_POOL_SIZE = 1;
    public static final Integer SMALL_KEEP_LIVE_TIME = 10;

    /**
     * medium
     */
    public static final Integer COMMON_CORE_POOL_SIZE = 3;
    public static final Integer COMMON_MAX_POOL_SIZE = 3;

    public static final Integer COMMON_KEEP_LIVE_TIME = 60;
    public static final Integer COMMON_QUEUE_SIZE = 128;


    /**
     * big
     */
    public static final Integer BIG_QUEUE_SIZE = 1024;
    public static final BlockingQueue BIG_BLOCKING_QUEUE = new LinkedBlockingQueue<>(BIG_QUEUE_SIZE);



}
