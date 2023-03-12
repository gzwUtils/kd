package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Accessors(chain = true)
@Data
public class WxErrorCode {

    private int errcode;

    private String errmsg;

    private String msgid;

}
