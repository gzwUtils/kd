package com.gzw.kd.learn.pipeline;

import com.gzw.kd.learn.pipeline.pipe.AbstractPipelineValue;
import com.gzw.kd.learn.pipeline.pipe.PipelineContext;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2024/1/17 22:58
 */
@Component
public class SwitchValue extends AbstractPipelineValue {
    @Override
    protected boolean doExec(PipelineContext pipelineContext) {
        pipelineContext.set("SwitchValue", true);
        return true;
    }
}
