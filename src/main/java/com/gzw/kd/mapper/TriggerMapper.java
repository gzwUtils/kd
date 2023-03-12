package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.Trigger;
import com.gzw.kd.common.entity.TriggerKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 高志伟
 */

@SuppressWarnings("all")
@Mapper
@Repository
public interface TriggerMapper {

    int deleteByPrimaryKey(TriggerKey key);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    Trigger selectByPrimaryKey(TriggerKey key);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKeyWithBLOBs(Trigger record);

    int updateByPrimaryKey(Trigger record);
}
