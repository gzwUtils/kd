package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2022/11/1 00:47
 */
@Accessors(chain = true)
@SuppressWarnings("all")
@Data
public class WxUserInfo {

    private Integer subscribe;

    private String openid;

    private String language;

    private String nickname;

    private String name;

    private String sex;

    private String headimgurl;

    private String phone;

    private long subscribe_time;

    private String unionid;

    private String remark;

    private String subscribe_scene;

    private Integer groupid;

    private int [] tagid_list;

    private String tagIdlist;

}
