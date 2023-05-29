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
public class Assign extends OrderInfo{

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
     * 剩余次数
     */
    private int syNumber;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 订阅服务ID
     */
    private String serviceId;


    public Assign() {
    }

    public Assign(String openId, String customerName, String phone, String address, LocalDateTime createTime, LocalDateTime updateTime, int syNumber, BigDecimal balance, String serviceId) {
        this.openId = openId;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.syNumber = syNumber;
        this.balance = balance;
        this.serviceId = serviceId;
    }
}
