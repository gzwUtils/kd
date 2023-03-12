package com.gzw.kd.assign.entuty;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */
@Accessors(chain = true)
@Data
public class PsnAuthConfig {

    /**
     * 未实名  需传此字段 个人用户账号标识（手机号或邮箱）
     */
    private String psnAccount;

    /**
     * 已实名 需传此字段  个人账号ID
     */
    private String psnId;

    /**
     * 个人身份附加信息
     */
    private PsnInfo psnInfo;

    /**
     * 个人实名认证页面配置项
     */
    private PsnAuthPageConfig psnAuthPageConfig;
}
