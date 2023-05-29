package com.gzw.kd.flowControl.service;

import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.entity.TaskInfo;

/**
 * @author gzw
 * @description： 流量控制服务
 * @since：2023/5/25 10:44
 */
public interface FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo info
     * @param flowControlParam flow
     * @return 耗费的时间
     */
    Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam);
}
