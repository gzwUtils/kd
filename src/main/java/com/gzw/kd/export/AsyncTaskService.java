package com.gzw.kd.export;

import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import java.time.LocalDateTime;

/**
 * 异步任务处理服务类，同1个异步任务类型下应该唯一
 *
 * @author gzw
 */


public interface AsyncTaskService {

    /**
     * 任务处理方法
     *
     * @param params     任务参数
     * @param creator    任务创建者
     * @param createTime 任务创建时间
     * @param fileName 文件名称
     * @return 异步任务返回值，可以为null，成功返回则代表执行成功
     */
    AsyncTaskOutput processAsyncTask(String params, String creator, LocalDateTime createTime,String fileName);

    /**
     * 是否支持该类型任务
     *
     * @param type 异步任务枚举
     * @return true支持，false不支持
     */
    boolean supportTask(AsyncTaskTypeEnum type);
}
