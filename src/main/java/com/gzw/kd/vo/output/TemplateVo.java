package com.gzw.kd.vo.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TemplateVo {

    /**
     * 模版id
     */
    private Long id;


    /**
     * 模板标题
     */
    @ApiModelProperty(value = "模板标题")
    private String name;


    /**
     * 模板内容
     */
    private String content;


    /**
     * 模版类型
     */

    private String type;


    /**
     * 状态
     */

    private String status;
}
