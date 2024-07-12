package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： 水印
 * @since：2023/6/29 16:40
 */
@Data
@Accessors(chain = true)
public class WaterMarkContent {

    /**
     * 是否添加
     */
    private boolean enable;

    /**
     * 内容
     */

    private String text;

    /**
     * 时间格式
     */

    private String dateFormat;

    /**
     * 颜色
     */

    private String color;
}
