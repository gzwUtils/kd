package com.gzw.kd.common.entity;

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
    private String annualBalance;

    /**
     * 每年费用
     */
    private String yearBalance;

    /**
     * 附加项费用
     */
    private String extraBalance;

    /**
     * 其他费用
     */
    private String otherBalance;
}
