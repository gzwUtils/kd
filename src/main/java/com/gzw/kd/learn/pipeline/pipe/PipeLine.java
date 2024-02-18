package com.gzw.kd.learn.pipeline.pipe;

/**
 * @author gzw
 * @description：
 * 管道(Pipeline)—-用于串联阀门的管道通路
 * 阀门(PipelineValue)—-用于每一个节点处理实际业务诉求
 * 管道上下文(PipelineContext)—-用于管道上下文中数据的扭转
 * @since：2024/1/17 22:42
 */
public interface PipeLine {

    /**
     * 执行
     * @param pipelineContext 管道上下文
     * @return res
     */
    boolean invoke(PipelineContext pipelineContext);

    /**
     * 添加值
     * @param pipelineValue 管道节点
     * @return res
     */
    boolean addValue(PipelineValue pipelineValue);
    /**
     * 移除值
     *
     * @param pipelineValue 管道节点
     * @return res
     */
    boolean removeValue(PipelineValue pipelineValue);
}
