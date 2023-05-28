package com.gzw.kd.send;

/**
 * @author gzw
 * @description： 责任链执行器
 * @since：2023/5/25 22:10
 */
public interface BusinessProcess<T extends ProcessModel> {

    /**
     * 真正处理逻辑
     *
     * @param context context
     */
    void process(ProcessContext<T> context);
}
