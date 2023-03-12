package com.gzw.kd.scheduletask;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author 高志伟
 */

@Slf4j
public class HelloJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("quartz HelloJob execute start --------------------------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("hello job execute error ",e);
        }
        log.info("quartz HelloJob execute end --------------------------");
    }
}
