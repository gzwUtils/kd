package com.gzw.kd.service;
import com.gzw.kd.common.entity.MessageRecord;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.vo.output.SendRecordVo;

import java.util.List;

public interface MessageRecordService {

    /**
     * 保存消息记录
     *
     * @param taskInfo 任务信息
     * @return res
     */
    Long createRecord(TaskInfo taskInfo);


    /**
     * 更新消息记录
     * @param recordId 记录ID
     * @param success 状态
     * @param successCount 成功数
     * @param failCount 失败数
     * @return res
     */


    boolean updateStatus(Long recordId, boolean success, int successCount, int failCount);





    /**
     * 根据查询用户发送记录
     * @return 发送记录
     */
    List<SendRecordVo> selectByAccount();




}
