package com.gzw.kd.export;

import com.gzw.kd.common.entity.AsyncTasksEntity;
import com.gzw.kd.common.enums.AsyncTaskStatusEnum;
import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import java.time.LocalDateTime;
import java.util.List;
import liquibase.pro.packaged.S;

/**
 * 异步任务日志服务类
 * @author gzw
 */
@SuppressWarnings("all")
public interface AsyncTaskLogService {

    /**
     * 添加一条异步任务日志
     *
     * @param paramsObject 参数对象
     * @param taskType     异步任务类型
     * @return 日志id
     */
    Long addOne(Object paramsObject, AsyncTaskTypeEnum taskType);

    /**
     * 添加一条异步任务日志
     *
     * @param paramsObject 参数对象
     * @param taskType     异步任务类型
     * @param exportName   导出名称
     * @param time         导出时间
     * @return 日志id  日志id
     */
    Long addOne(Object paramsObject, AsyncTaskTypeEnum taskType, String exportName, LocalDateTime time);

    /**
     * 更新一条异步任务日志
     *
     * @param id              主键id
     * @param taskStatus      任务状态
     * @param asyncTaskOutput 任务输出
     * @param filePath 文件路径
     * @param exception       异常信息
     */
    void updateOne(Long id, AsyncTaskStatusEnum taskStatus, AsyncTaskOutput asyncTaskOutput, String exception, String filePath);

    /**
     * 获取未完成的异步任务
     *
     * @param size 预期获取的条目数
     * @return 未处理的异步任务
     */
    List<AsyncTasksEntity> fetchUnCompletedTasks(int size);

}
