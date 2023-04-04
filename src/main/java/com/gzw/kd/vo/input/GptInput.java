package com.gzw.kd.vo.input;

import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:06
 */
@Data
public class GptInput {

    /**
     * 问题
     */
    private String askStr;

    /**
     * 回答
     */
    private String replyStr;
}
