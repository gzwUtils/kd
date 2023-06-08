package com.gzw.kd.service;

import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.XxlJobGroup;
import com.gzw.kd.common.entity.XxlJobInfo;

/**
 * @author gzw
 * @description： 定时任务服务
 * @since：2023/6/5 15:34
 */
public interface CronTaskService {

    /**
     * 新增/修改 定时任务
     *
     * @param xxlJobInfo info
     * @return 新增时返回任务Id，修改时无返回
     */
    R saveCronTask(XxlJobInfo xxlJobInfo);

    /**
     * 删除定时任务
     *
     * @param taskId taskId
     * @return result
     */
    R deleteCronTask(Integer taskId);

    /**
     * 启动定时任务
     *
     * @param taskId taskId
     * @return result
     */
    R startCronTask(Integer taskId);


    /**
     * 暂停定时任务
     *
     * @param taskId taskId
     * @return result
     */
    R stopCronTask(Integer taskId);


    /**
     * 得到执行器Id
     *
     * @param appName appName
     * @param title title
     * @return result
     */
    Integer getGroupId(String appName, String title);

    /**
     * 创建执行器
     *
     * @param xxlJobGroup xxlJobGroup
     * @return result
     */
    R createGroup(XxlJobGroup xxlJobGroup);
}
