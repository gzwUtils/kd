package com.gzw.kd.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.XxlJobConstant;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.common.entity.XxlJobGroup;
import com.gzw.kd.common.entity.XxlJobInfo;
import com.gzw.kd.common.enums.ExecutorRouteStrategyEnum;
import com.gzw.kd.common.enums.MisfireStrategyEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.enums.ScheduleTypeEnum;
import com.gzw.kd.service.CronTaskService;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： xxl-job
 * @since：2023/6/5 14:13
 */
@SuppressWarnings("unused")
@Component
@Slf4j
public class XxlJobUtils {

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Resource
    private CronTaskService cronTaskService;


    /**
     * 构建xxlJobInfo信息
     *
     * @param templateInfo templateInfo
     * @return xxl info
     */

    public XxlJobInfo buildXxlJobInfo(TemplateInfo templateInfo) {

        String scheduleConf = templateInfo.getExpectPushTime();
        // 如果没有指定cron表达式，说明立即执行(给到xxl-job延迟5秒的cron表达式)
        if (templateInfo.getExpectPushTime().equals(STRING_EMPTY)) {
            scheduleConf = DateUtil.format(DateUtil.offsetSecond(new Date(), XxlJobConstant.DELAY_TIME), XxlJobConstant.CRON_FORMAT);
        }


        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(queryJobGroupId()).jobDesc(templateInfo.getName())
                .author(templateInfo.getCreator())
                .scheduleConf(scheduleConf)
                .scheduleType(ScheduleTypeEnum.CRON.name())
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name())
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name())
                .executorHandler(XxlJobConstant.JOB_TITLE)
                .executorParam(String.valueOf(templateInfo.getId()))
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name())
                .executorTimeout(XxlJobConstant.TIME_OUT)
                .executorFailRetryCount(XxlJobConstant.RETRY_COUNT)
                .triggerStatus(INT_ZERO)
                .alarmEmail(StrUtil.EMPTY)
                .childJobId(StrUtil.EMPTY).build();

        if (Objects.nonNull(templateInfo.getCronTaskId())) {
            xxlJobInfo.setId(templateInfo.getCronTaskId());
        }
        return xxlJobInfo;
    }

    /**
     * 根据就配置文件的内容获取jobGroupId，没有则创建
     *
     * @return ll
     */
    private Integer queryJobGroupId() {
        String jobHandlerName = XxlJobConstant.JOB_TITLE;
        Integer groupId = cronTaskService.getGroupId(appName, jobHandlerName);
        if (groupId ==null) {
            XxlJobGroup xxlJobGroup = XxlJobGroup.builder().appName(appName).title(jobHandlerName).addressType(INT_ZERO).build();
            if (ResultCodeEnum.SUCCESS.getCode().equals(cronTaskService.createGroup(xxlJobGroup).getCode())) {
                return cronTaskService.getGroupId(appName, jobHandlerName);
            }
        }
        return groupId;
    }
}
