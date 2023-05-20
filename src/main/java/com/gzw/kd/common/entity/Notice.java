package com.gzw.kd.common.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2023/5/20 17:29
 */
@Accessors(chain = true)
@Data
public class Notice {

    /**id*/
    private int id;


    /** 公告内容*/
    private String context;


    /**操作人*/
    private String operatorName;

    /**创建时间*/
    private LocalDateTime createTime;
}
