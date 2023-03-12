package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.JobAndTrigger;
import com.gzw.kd.common.entity.JobDetail;
import com.gzw.kd.common.entity.JobDetailKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Mapper
@Repository
public interface JobDetailMapper {

    /**
     * 删除job detail
     * @param key detail
     ** @return
     */
    int deleteByPrimaryKey(JobDetailKey key);

    /**
     *  插入 job detail
     * @param record
     * @return
     */
    int insert(JobDetail record);

    /**
     *  插入 job detail
     * @param record
     * @return
     */
    int insertSelective(JobDetail record);

    /**
     *  cha
     * @param key
     * @return
     */
    JobDetail selectByPrimaryKey(JobDetailKey key);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(JobDetail record);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithBLOBs(JobDetail record);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(JobDetail record);

    /**
     * 获取
     * @return
     */
    List<JobAndTrigger> getJobAndTriggerDetails();
}
