package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/14
 * @dec
 */

@Accessors(chain = true)
@Data
public class Assign {


    /**
     *id
     */

    private int  id ;

    /**
     * 0 未支付 1  支付
     */

    private int  status ;

    /**
     *openId
     */
    private String openId;


    /**
     * 姓名
     */
    private String customerName;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 消费次数
     */
    private String number;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 订阅服务ID
     */
    private String serviceId;

    /**
     * 是否拥有优惠劵 0 没有 1 有
     */
    private int coupon;

    /**
     * 优惠劵额度
     */
    private BigDecimal couponBalance;

}
