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
public class InnerApiInput {
    /**
     * 时间戳，毫秒
     */
    private String timestamp;

    /**
     * 消息签名
     */
    private String sign;

    /**
     * token
     */
    private String token;

    /**
     * body的json格式
     */
    private String body;
}
