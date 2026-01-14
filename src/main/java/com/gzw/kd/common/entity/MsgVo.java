package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgVo {


    private String type;      // 消息类型: private, room, file, sys等
    private String from;      // 发送者
    private String to;        // 接收者
    private String content;   // 消息内容
    private Long time;   // 时间戳

    // 文件相关字段
    private String fileName;  // 文件名
    private String fileType;  // 文件类型
    private String fileData;  // 文件数据(base64)



    public MsgVo(String type, String from, String to, String content, Long timestamp) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
        this.time = timestamp;
    }
}
