package com.gzw.kd.service;

import com.gzw.kd.common.entity.SysLog;

/**
 * @author 高志伟
 */
public interface SystemOperationLogService {

    /**
     * 插入日志
     * @param record record
     * @return int
     */
    int insertSelective(SysLog record);
}
