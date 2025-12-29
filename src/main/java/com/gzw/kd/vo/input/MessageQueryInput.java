package com.gzw.kd.vo.input;


import lombok.Data;

@Data
public class MessageQueryInput {

    // all, unread, MessageContentTypeEnum
    private String type = "all";
    // 搜索关键词
    private String keyword;

    // newest, oldest
    private String sortBy = "newest";

    // 接收者ID（对应 receiver 字段）
    private String userId;

    // 手机号
    private String phone;

    // 邮件
    private String email;


    private Integer pageNum = 1;


    private Integer pageSize = 5;
}
