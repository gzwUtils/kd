package com.gzw.kd.service.impl;

import com.gzw.kd.common.entity.SysLog;
import com.gzw.kd.mapper.SystemOperationLogMapper;
import com.gzw.kd.service.SystemOperationLogService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author 高志伟
 */

@Service
public class SystemOperationLogServiceImpl  implements SystemOperationLogService {

    @Resource
    SystemOperationLogMapper systemOperationLogMapper;

    @Override
    public int insertSelective(SysLog record) {
        return systemOperationLogMapper.insertSelective(record);
    }
}
