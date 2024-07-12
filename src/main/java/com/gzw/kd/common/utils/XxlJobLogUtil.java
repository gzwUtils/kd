package com.gzw.kd.common.utils;

import com.xxl.job.core.context.XxlJobHelper;
import org.slf4j.Logger;

/**
 * @author gzw
 * @description： xxl log
 * @since：2023/6/4 14:47
 */
@SuppressWarnings("all")
public class XxlJobLogUtil {


    /**
     * @param log     xxl-job任务处理类中声明的log对象
     * @param isError 是否是错误日志
     * @param msg     日志消息
     * @param params  日志参数
     */

    public static void log(Logger log, boolean isError, String msg, Object... params) {
        XxlJobHelper.log(msg, params);
        if (isError) {
            log.error(msg, params);
        } else {
            log.info(msg, params);
        }
    }

    /**
     * 非多线程环境下的XxlJob日志
     *
     * @param log xxl-job任务处理类中声明的log对象
     * @param msg 日志消息
     * @param e   异常对象
     */
    public static void log(Logger log, String msg, Exception e) {
        XxlJobHelper.log(e);
        log.error(msg, e);
    }

    /**
     * 主要用于多线程内XxlJob的日志记录，需要在异步线程内重置日志文件名称，否则后面日志清理后将报文件找不到异常
     * XxlJobFileAppender.contextHolder.set(xxlJobLogFileName)
     *
     * @param log     xxl-job任务处理类中声明的log对象
     * @param isError 是否是错误日志
     * @param msg     日志消息
     * @param params  日志参数
     */
    public static void syncLog(Logger log, boolean isError, String msg, Object... params) {
        synchronized (log) {
            log(log, isError, msg, params);
        }
    }

    /**
     * 主要用于多线程内XxlJob的日志记录，需要在异步线程内重置日志文件名称，否则后面日志清理后将报文件找不到异常
     * XxlJobFileAppender.contextHolder.set(xxlJobLogFileName)
     *
     * @param log xxl-job任务处理类中声明的log对象
     * @param msg 日志消息
     * @param e   异常对象
     */
    public static void syncLog(Logger log, String msg, Exception e) {
        synchronized (log) {
            log(log, msg, e);
        }
    }
}
