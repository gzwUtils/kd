package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */
@Accessors(chain = true)
@Data
public class TemplateDataStyle {

    private String value;
    private String color;

    public TemplateDataStyle( String value) {
        this.color = "#173177";
        this.value = value;
    }

}
