package com.gzw.kd.vo.input;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/14
 * @dec
 */

@Accessors(chain = true)
@Data
public class WxUserInput {
    /**
     *openId
     */
    private String openId;


    /**
     * 姓名
     */
    private String name;


    /**
     * 手机号
     */
    private String phone;

}
