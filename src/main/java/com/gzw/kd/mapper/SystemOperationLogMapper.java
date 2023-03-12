package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 高志伟
 */
@Repository
@Mapper
public interface SystemOperationLogMapper {

    /**
     * 插入日志
     * @param record record
     * @return int
     */
    int insertSelective(SysLog record);
}
