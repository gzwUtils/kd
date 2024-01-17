package com.gzw.kd.learn.pipeline.standardPipe;

import com.alibaba.google.common.collect.Lists;
import com.gzw.kd.learn.pipeline.pipe.PipeLine;
import com.gzw.kd.learn.pipeline.pipe.PipelineContext;
import com.gzw.kd.learn.pipeline.pipe.PipelineValue;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 管道-+
 * @since：2024/1/17 22:46
 */
@Slf4j
public class StandardPipeline implements PipeLine {

    private final List<PipelineValue> pipelineValueList = Lists.newArrayList();

    @Override
    public boolean invoke(PipelineContext pipelineContext) {
        boolean isResult = true;
        for (PipelineValue pipelineValue :pipelineValueList) {
            try {
                isResult = pipelineValue.execute(pipelineContext);
                if (!isResult) {
                    log.error("{},exec is wrong", pipelineValue.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return isResult;
    }

    @Override
    public boolean addValue(PipelineValue pipelineValue) {
        if (pipelineValueList.contains(pipelineValue)) {
            return true;
        }
        return pipelineValueList.add(pipelineValue);
    }

    @Override
    public boolean removeValue(PipelineValue pipelineValue) {
        return pipelineValueList.remove(pipelineValue);
    }
}
