package com.gzw.kd.learn.pipeline;

import com.gzw.kd.learn.pipeline.pipe.AbstractPipelineValue;
import com.gzw.kd.learn.pipeline.pipe.PipelineContext;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2024/1/17 22:59
 */
@Component
public class TestValue extends AbstractPipelineValue {
    @Override
    protected boolean doExec(PipelineContext pipelineContext) {
        return true;
    }
}
