package com.gzw.kd.scheduletask;

import com.gzw.kd.common.thread.ThreadPoolConfig;
import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.common.utils.ThreadUtils;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author 高志伟
 */
@Slf4j
@Component
public class HelloJob implements BaseJob {


   private static final ThreadPoolExecutor POOL_EXECUTOR= ThreadPoolConfig.getJobExecutor();



    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("quartz HelloJob execute start --------------------------");
        ThreadUtils threadUtils = ApplicationContextUtils.getBean(ThreadUtils.class);
        threadUtils.register(POOL_EXECUTOR);
        POOL_EXECUTOR.execute(() -> log.info("一生挚爱  回头太难-------------------------------------"));
        log.info("quartz HelloJob execute end --------------------------");
    }
}
