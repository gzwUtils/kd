package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:44
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class EmailContentModel extends  ContentModel {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容(可写入HTML)
     */
    private String content;

    /**
     * 邮件附件链接
     */
    private String url;


    /**
     * 邮件发送时间
     */
    private String sendTime;


    /**
     * 邮件发送用户
     */
    private String name;
}
