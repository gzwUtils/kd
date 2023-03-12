package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.CronTrigger;
import com.gzw.kd.common.entity.CronTriggerKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Mapper
@Repository
public interface CronTriggerMapper {

    int deleteByPrimaryKey(CronTriggerKey key);

    int insert(CronTrigger record);

    int insertSelective(CronTrigger record);

    CronTrigger selectByPrimaryKey(CronTriggerKey key);

    int updateByPrimaryKeySelective(CronTrigger record);

    int updateByPrimaryKey(CronTrigger record);
}
