package com.gzw.kd.common.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gzw.kd.common.entity.JobAndTrigger;
import com.gzw.kd.mapper.JobDetailMapper;
import com.gzw.kd.scheduletask.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class QuartzManager{


    private static QuartzManager jobUtil;

    @Resource
    private Scheduler scheduler;

    @Resource
    private JobDetailMapper jobDetailMapper;

    public QuartzManager() {
        jobUtil = this;
    }

    public static QuartzManager getInstance() {
        return QuartzManager.jobUtil;
    }






    public PageInfo<JobAndTrigger> getJobAndTriggerDetails(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<JobAndTrigger> list = jobDetailMapper.getJobAndTriggerDetails();
        return new PageInfo<>(list);
    }

    /**
     * 新增定时任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @param tName  触发器名称
     * @param tGroup 触发器组
     * @param cron   cron表达式
     */

    public void addJob(String jName, String jGroup, String tName, String tGroup, String cron, String jobClass) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(getClass(jobClass).getClass())
                    .withIdentity(jName, jGroup).storeDurably()
                    .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            // 启动调度器
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("创建定时任务失败 jobClass {}",jobClass,  e);
        }
    }


    public void pauseJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jName, jGroup));
    }


    public void resumeJob(String jName, String jGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jName, jGroup));
    }


    public void rescheduleJob(String jName, String jGroup, String cron) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jName, jGroup);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);
    }


    public void deleteJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jName, jGroup));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jName, jGroup));
        scheduler.deleteJob(JobKey.jobKey(jName, jGroup));
    }


    public static BaseJob getClass(String className) throws Exception {
        Class<?> aClass = Class.forName(className);
        return (BaseJob)aClass.newInstance();
    }
}
