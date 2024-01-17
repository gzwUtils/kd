package com.gzw.kd.learn.pipeline.pipe;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

/**
 * @author gzw
 * @description：
 * @since：2024/1/17 22:52
 */
@Slf4j
public abstract class AbstractPipelineValue implements PipelineValue {

    @Override
    public boolean execute(PipelineContext pipelineContext) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("{} start......",this.getClass().getSimpleName());
        boolean result = doExec(pipelineContext);
        stopWatch.stop();
        log.info("{} end.. 耗时:{}ms....",this.getClass().getSimpleName(),stopWatch.getTime(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     *  execute
     * @param pipelineContext 上下文
     * @return res
     */
    protected abstract boolean doExec(PipelineContext pipelineContext);

}
