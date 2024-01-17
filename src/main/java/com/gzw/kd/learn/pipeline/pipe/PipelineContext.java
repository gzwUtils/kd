package com.gzw.kd.learn.pipeline.pipe;

/**
 * @author gzw
 * @description： 管道上下文
 * @since：2024/1/17 22:43
 */
public interface PipelineContext {

    /**
     * 设置
     *
     * @param contextKey key
     * @param contextValue value
     */
    void set(String contextKey, Object contextValue);
    /**
     * 获取值
     *
     * @param contextKey key
     * @return object
     */
    Object get(String contextKey);
}
