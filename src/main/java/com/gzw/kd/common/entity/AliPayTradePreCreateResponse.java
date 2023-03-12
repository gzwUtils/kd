package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */

@Data
@SuppressWarnings("all")
public class AliPayTradePreCreateResponse {

    private String code;
    private String msg;
    private String out_trade_no;
    private String qr_code;
}
