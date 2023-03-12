package com.gzw.kd.common.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Data
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 菜单标题
     */
    private String name;

    /**
     * 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     */
    private String type;

    /**
     * click等点击类型必须	菜单 KEY 值，用于消息接口推送
     */
    private String key;

    /**
     * 网页链接，用户点击菜单可打开链接，不超过1024字节。当 type 为miniprogram时，不支持小程序的老版本客户端将打开本url
     */

    private String url;

    /**
     * 菜单级别
     */

    private Integer menuLevel;

    /**
     * 菜单标题
     */

    private String upMenuName;


    /**
     * 用户标签ID
     */
    private String tag_id;

    /**
     * 菜单匹配规则
     */
    private Object matchrule;


    private List<Menu> twoMenuList;
}
