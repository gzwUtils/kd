package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:08
 */

@Data
public class GptChoice {

    private String text;

    private Integer index;
}
