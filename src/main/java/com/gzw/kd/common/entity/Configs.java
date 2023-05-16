package com.gzw.kd.common.entity;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author 高志伟
 */

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
}
