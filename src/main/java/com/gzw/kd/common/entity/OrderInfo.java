package com.gzw.kd.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 高志伟
 */

@Data
public class OrderInfo {

    /**id */
    private int  id ;

    /**服务期限*/
    private int  serviceYear;

    /**起始时间*/
    private LocalDateTime startTime;

    /**终止时间*/
    private LocalDateTime  endTime;

    /**房屋住宅面积*/
    private String  floorArea;

    /**服务分类*/
    private int  style;

    /**服务次数*/
    private int  number;

    /**第一次服务时间*/
    private LocalDateTime  firstServiceTime;

    /**固定服务时间*/
    private LocalDateTime  serviceTime;

    /**附加项目*/
    private String  extraDesc;

    /**总费用*/
    private BigDecimal serviceBalance;

    /**openId*/
    private String  openid;
}
