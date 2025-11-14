package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgVo {

    private String type;   // group/private/sys/selfInfo/userList
    private String from;
    private String to;
    private String content;
    private long time;
}
