package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.AsyncTasksEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高志伟
 */

@Mapper
@Repository
public interface AsyncTasksLogMapper {

    /**
     * 插入异步任务
     * @param asyncTasks asyncTasks
     * @return int
     */

    Integer insertOne(AsyncTasksEntity asyncTasks);

    /**
     * 更新异步任务
     * @param asyncTasks asyncTasks
     * @return int
     */
    Integer updateOne(AsyncTasksEntity asyncTasks);

    /**
     * 查询异步任务
     * @param status 未处理
     * @param size 条数
     * @return int
     */

    List<AsyncTasksEntity> selectAll(int status,int size);
}
