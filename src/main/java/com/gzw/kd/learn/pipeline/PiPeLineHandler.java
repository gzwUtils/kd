package com.gzw.kd.learn.pipeline;

import com.gzw.kd.learn.pipeline.pipe.PipeLine;
import com.gzw.kd.learn.pipeline.pipe.PipelineContext;
import com.gzw.kd.learn.pipeline.standardPipe.StandardPipeline;
import com.gzw.kd.learn.pipeline.standardPipe.StandardPipelineContext;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 执行
 * @since：2024/1/17 23:00
 */
@Component
public class PiPeLineHandler {

    @Resource
    private SwitchValue switchValue;

    @Resource
    private TestValue testValue;


    public void exec(){
        // 管道初始化
        PipeLine pipeline = new StandardPipeline();

        //添加节点
        pipeline.addValue(testValue);
        pipeline.addValue(switchValue);

        // 上下文
        PipelineContext pipelineContext = new StandardPipelineContext();

        // 调用管道
        pipeline.invoke(pipelineContext);

    }
}
