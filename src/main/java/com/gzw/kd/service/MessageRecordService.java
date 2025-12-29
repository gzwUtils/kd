package com.gzw.kd.service;
import com.github.pagehelper.PageInfo;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.vo.input.MessageQueryInput;
import com.gzw.kd.vo.output.MessageCountOutput;
import com.gzw.kd.vo.output.MessageOutput;
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


    /**
     * 根据查询用户消息
     * @return 消息列表
     */
    PageInfo<MessageOutput> getMessages(MessageQueryInput queryInput);
    /**
     * 根据ID查询消息
     * @param id 消息ID
     * @return 消息
     */
    MessageOutput getMessageById(Long id);

    /**
     * 标记消息为已读
     * @param id 消息ID
     */
    void markAsRead(Long id);
    /**
     * 批量标记消息为已读
     */
    void markAllAsRead();

    /**
     * 删除消息
     * @param id 消息ID
     */
    void deleteMessage(Long id);

    /**
     * 批量删除消息
     */
    void clearAllMessages();


    /**
     * 获取消息数量
     * @return 消息数量
     */
    MessageCountOutput getMessageCount();




}
