package com.gzw.kd.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */
@Accessors(chain = true)
@Data
public class SysLog implements Serializable {

    private static final long serialVersionUID = -8878596941954995444L;


    private int  id;


    private String username;


    private String operation;

    private String desc;


    private Long time;


    private String method;


    private String params;


    private String ip;


    private LocalDateTime createTime;


    private String location;

    private String result;

}
