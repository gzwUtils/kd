package com.gzw.kd.vo.input;

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
public class AssignInput {

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
     * 房屋建筑面积
     */
    private String floorArea;

    /**
     * 服务方式
     */
    private int style;

    /**
     * 服务次数
     */
    private int number;

    /**
     * 第一次日期
     */
    private LocalDateTime firstServiceTime;

    /**
     * 第一次日期 上下午
     */
    private LocalDateTime endDay;

    /**
     * 固定日期
     */
    private LocalDateTime serviceTime;

    /**
     * 固定日期 上下午
     */
    private LocalDateTime serviceEndDay;

    /**
     * 特许附加项目
     */
    private String extraDesc;
    /**
     * 优惠劵额度
     */
    private BigDecimal couponBalance;

}
