package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.MessageRecord;
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
    List<MessageRecord> selectByTemplateId(Long templateId);
}