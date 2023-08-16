package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author 高志伟 {{.DATA}}
 */
@Accessors(chain = true)
@Data
public class WeChatTemplateMsg {


    /**
     * 消息颜色
     */
    private String color;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板ID
     */
    private String templateId;


    /**
     * 0 关注  1  通知 2 提醒  3 其他
     */
    private Integer role;


    /**
     *  模版ID
     */
    private Integer id;


    /**
     *  数据映射
     */
    private String dataFormat;

    /**
     *  来源
     */
    private String sys;



    /**
     * 启用 停用
     */
    private Integer status;




    /* -----------------------------template  push ------------------*/

    /**
     * 模板数据
     */
    private Map<String,TemplateDataStyle> data;

    /**
     * 跳转链接
     */
    private String url;


    /**
     * openId
     */
    private String toUser;


    public WeChatTemplateMsg() {
        this.color = "#173177";
    }



}
