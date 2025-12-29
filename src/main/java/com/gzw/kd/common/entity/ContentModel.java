package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author gzw
 * @description： 责任链 抽象实体
 * @since：2023/5/24 15:13
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ContentModel {

    /**
     * 发送内容
     */
    private String content;


    /**
     * 标题
     */
    private String title;


    /**
     * 邮件发送时间
     */
    private String sendTime;
}
