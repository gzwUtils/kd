package com.gzw.kd.scheduletask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.gzw.kd.common.entity.AsyncTasksEntity;
import com.gzw.kd.common.entity.SysLog;
import com.gzw.kd.common.enums.AsyncTaskStatusEnum;
import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.utils.AddressUtil;
import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.common.utils.IpUtil;
import com.gzw.kd.common.utils.XxlJobLogUtil;
import com.gzw.kd.export.AsyncTaskExecutorService;
import com.gzw.kd.export.AsyncTaskLogService;
import com.gzw.kd.export.AsyncTaskService;
import com.gzw.kd.service.SystemOperationLogService;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import static com.gzw.kd.common.Constants.STRING_EMPTY;

/**
 * 异步任务处理定时任务, 单机串行执行,
 *
 * @author gzw
 */
@DependsOn({"applicationContextUtils"})
@Component
@Slf4j
public class AsyncTaskJobHandler {

    private final Collection<AsyncTaskService> asyncTaskHandlers;

    @Resource
    AsyncTaskLogService asyncTaskLogService;


    @Resource
    SystemOperationLogService systemOperationLogService;
    @Resource
    AsyncTaskExecutorService asyncTaskExecutorService;

    public AsyncTaskJobHandler() {
        asyncTaskHandlers =ApplicationContextUtils.getApplicationContext().getBeansOfType(AsyncTaskService.class).values();
        log.info("Async task impl list: {}", asyncTaskHandlers);
    }

    public void handle() {
        List<AsyncTasksEntity> asyncTaskLogs = asyncTaskLogService.fetchUnCompletedTasks(3);

        if (CollectionUtil.isNotEmpty(asyncTaskLogs)) {
            asyncTaskLogs.forEach(logs -> {
                // 提交任务至线程池
                final AsyncTaskTypeEnum type = AsyncTaskTypeEnum.getEnumByCode(logs.getType());
                asyncTaskHandlers.stream()
                        // 过滤支持处理该日志的task
                        .filter(t -> t.supportTask(type))
                        .forEach(h -> {

                            try {
                                log.info("async task execute  menu {} type {}",logs.getFromMenu(),logs.getType());

                                // 记录任务处理状态为处理中
                                asyncTaskLogService.updateOne(logs.getId(), AsyncTaskStatusEnum.PROCESSING
                                        , null, null,null);
                                // 提交任务至线程池中
                                asyncTaskExecutorService.submitTask(
                                        // 执行asyncTask实现类processAsyncTask方法
                                        () -> {
                                            AsyncTaskOutput output = h.processAsyncTask(logs.getParams(), logs.getCreator(), logs.getCreateTime(), logs.getExportName());
                                            updateLogDetails(type, logs.getId(), logs.getParams(), output,logs.getCreator());
                                            return output;
                                        },
                                        ret -> asyncTaskLogService.updateOne(logs.getId()
                                                , taskExecuteStatus(ret)
                                                , ret.getAsyncTaskOutput()
                                                , exceptionMessage(ret),ret.getAsyncTaskOutput().getFilePath()));
                            } catch (RejectedExecutionException ex) {
                                asyncTaskLogService.updateOne(logs.getId(), AsyncTaskStatusEnum.FAILURE
                                        , null, ex.getMessage(),null);
                                log.error("async task error {} ", ex.getMessage(), ex);
                            }
                        });
            });
        }
    }

    private AsyncTaskStatusEnum taskExecuteStatus(AsyncTaskExecutorService.AsyncRunnableResult ret) {
        return !ret.isOccursException() ? AsyncTaskStatusEnum.SUCCESS : AsyncTaskStatusEnum.FAILURE;
    }

    private String exceptionMessage(AsyncTaskExecutorService.AsyncRunnableResult ret) {
        // 记录异常堆栈用于排查
        return ret.isOccursException() ? ExceptionUtil.stacktraceToString(ret.getEx(), 2000) : STRING_EMPTY;
    }


    private void updateLogDetails(AsyncTaskTypeEnum type, Long id, String params, AsyncTaskOutput asyncTask,String creator) {
        try {
            if (asyncTask == null) {
                log.error("异步任务补充详细日志失败,asyncId:{},asyncTask is null", id);
                return;
            }

            if (type == AsyncTaskTypeEnum.ALL_LOG_EXPORT || type == AsyncTaskTypeEnum.ADD_LOG_EXPORT) {
                Long ofSuccesses = asyncTask.getNumberOfSuccesses();
                SysLog sysLog = new SysLog();
                String desc = "id "+id+" success:"+ofSuccesses +" fail:"+asyncTask.getNumberOfFailed()+" fileName:"+asyncTask.getFileName();
                sysLog.setUsername(creator);
                sysLog.setResult(ResultCodeEnum.SUCCESS.getMessage());
                sysLog.setIp(Objects.requireNonNull(IpUtil.getLocalIp()).getHostAddress()).setParams(params).setDesc(desc).setOperation("async-task-export").setCreateTime(LocalDateTime.now()).setLocation(AddressUtil.getAddress(sysLog.getIp()));
                systemOperationLogService.insertSelective(sysLog);
            }
        } catch (Exception e) {
            log.error("异步任务补充详细日志失败,asyncId:{}", id, e);
        }
    }

    @XxlJob("AsyncTaskJobHandler")
    public void execute() throws JobExecutionException {
        XxlJobLogUtil.log(log,false,"异步任务开始执行");
        handle();
        XxlJobLogUtil.log(log,false,"异步任务执行结束");
    }
}
