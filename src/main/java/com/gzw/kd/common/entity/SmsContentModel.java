package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
@SuperBuilder
public class SmsContentModel extends ContentModel{

    /**
     * 短信发送链接
     */
    private String url;
}
