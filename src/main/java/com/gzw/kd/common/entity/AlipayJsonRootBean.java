package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */
@Data
@SuppressWarnings("all")
public class AlipayJsonRootBean {

    private AliPayTradePreCreateResponse alipay_trade_precreate_response;
    private String sign;
}
