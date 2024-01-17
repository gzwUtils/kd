package com.gzw.kd.learn.pipeline.pipe;

/**
 * @author gzw
 * @description： 管道节点
 * @since：2024/1/17 22:45
 */
public interface PipelineValue {

    /**
     * 节点执行
     *
     * @param pipelineContext pipe
     * @return
     */
    boolean execute(PipelineContext pipelineContext);

}
