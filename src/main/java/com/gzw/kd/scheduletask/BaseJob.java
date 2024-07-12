package com.gzw.kd.scheduletask;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * @author 高志伟
 */
public interface BaseJob extends Job {

    void execute(JobExecutionContext executionContext) throws JobExecutionException;
}
