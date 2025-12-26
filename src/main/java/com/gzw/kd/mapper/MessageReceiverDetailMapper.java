package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.MessageReceiverDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MessageReceiverDetailMapper {

    // 插入详情记录
    int insertDetail(MessageReceiverDetail detail);

    // 批量插入
    int batchInsert(@Param("list") List<MessageReceiverDetail> details);

    // 根据记录ID查询
    List<MessageReceiverDetail> selectByRecordId(Long recordId);

    // 标记为已读
    int markAsRead(@Param("id") Long id, @Param("receiver") String receiver);

    // 统计未读数量

    int countUnread(@Param("receiver") String receiver);
}