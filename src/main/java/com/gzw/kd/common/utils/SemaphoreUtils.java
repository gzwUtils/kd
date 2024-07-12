package com.gzw.kd.common.utils;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 信号量
 * @since：2024/2/3 23:22
 */
@SuppressWarnings("unused")
@Slf4j
public class SemaphoreUtils {

    /**
     * 获取信号量
     *
     * @param semaphore semaphore
     * @return res
     */
    public static boolean tryAcquire(Semaphore semaphore) {
        boolean flag = false;

        try {
            flag = semaphore.tryAcquire();
        } catch (Exception e) {
            log.error("获取信号量异常", e);
        }
        return flag;
    }

    /**
     * 释放信号量
     *
     * @param semaphore semaphore
     */
    public static void release(Semaphore semaphore) {

        try {
            semaphore.release();
        } catch (Exception e) {
            log.error("释放信号量异常", e);
        }
    }
}
