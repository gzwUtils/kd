package com.gzw.kd.scheduletask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author 高志伟
 */
@Slf4j
@Component
public class LearnJob implements BaseJob {





    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("quartz LearnJob execute start --------------------------");

        log.info("quartz LearnJob execute end --------------------------");
    }
}
