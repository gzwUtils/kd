package com.gzw.kd.common.entity;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContentModel extends  ContentModel {




    /**
     * 邮件发送用户
     */
    private String name;


    /**
     * 邮件附件链接
     */
    private String url;


}
