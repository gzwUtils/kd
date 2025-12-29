package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.MessageRecord;
import com.gzw.kd.vo.input.MessageQueryInput;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageRecordMapper {

    /**
     * 插入消息记录
     */
    int insertRecord(MessageRecord messageRecord);

    /**
     * 根据ID查询
     */
    MessageRecord selectById(Long id);

    /**
     * 根据业务ID查询
     */
    MessageRecord selectByBizId(String bizId);


    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("sendTime") Date sendTime);

    /**
     * 更新统计信息
     */
    int updateStatistics(@Param("id") Long id,
                         @Param("successCount") Integer successCount,
                         @Param("failCount") Integer failCount);

    /**
     * 增加重试次数
     */
    int incrementRetryCount(Long id);


    /**
     * 根据模板ID查询
     * @param templateId 模板ID
     * @return 消息记录
     */
    List<MessageRecord> selectByTemplateId(Long templateId,@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);


    // 根据ID和用户查询消息详情
    MessageRecord selectByIdAndUser(@Param("id") Long id, @Param("userId") String userId);

    // 查询用户消息列表
    List<MessageRecord> selectUserMessages(MessageQueryInput queryInput);

    // 统计用户消息数量
    Integer countUserMessages(MessageQueryInput queryInput);

    // 统计总消息数
    Integer countByUser(@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);

    // 统计未读消息数
    Integer countUnreadByUser(@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);

    // 统计系统通知数
    Integer countSystemByUser(@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);

    // 统计告警消息数
    Integer countSecurityByUser(@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);

    // 统计公告消息数
    Integer countUpdateByUser(@Param("userId") String userId, @Param("email") String email,@Param("phone") String phone);

    // 更新阅读状态
    int updateReadStatus(@Param("recordId") Long recordId, @Param("receiver") String receiver);

    // 删除用户消息
    int deleteUserMessage(@Param("recordId") Long recordId);

    // 删除用户所有消息
    int deleteAllUserMessages(@Param("receiver") String receiver, @Param("email") String email,@Param("phone") String phone);
}