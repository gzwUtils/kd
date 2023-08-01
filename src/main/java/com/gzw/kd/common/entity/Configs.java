package com.gzw.kd.common.entity;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */
@Accessors(chain = true)
@Data
public class Configs {

    /**
     * id
     */
    private Integer  id;

    /**
     * 每月费用
     */
    private BigDecimal annualBalance;

    /**
     * 每年费用
     */
    private BigDecimal yearBalance;

    /**
     * 附加项费用
     */
    private BigDecimal extraBalance;

    /**
     * 其他费用
     */
    private BigDecimal otherBalance;



    private String createTime;


    private String updateTime;
}
