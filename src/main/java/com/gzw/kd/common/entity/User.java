package com.gzw.kd.common.entity;

import com.gzw.kd.common.annotation.PrivacyEncrypt;
import com.gzw.kd.common.enums.PrivacyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/9
 * @dec
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空")
    private int  id;

    @NotBlank(message="姓名不能为空")
    @Length(min = 2, max = 10, message = "name 姓名长度必须在 {min} - {max} 之间")
    @PrivacyEncrypt(type = PrivacyTypeEnum.NAME)
    private String  account;

    /**
     * 邮箱
     */
    @PrivacyEncrypt(type = PrivacyTypeEnum.EMAIL)
    private String email;

    /**
     * 密码
     */

    private String password;


    private String  timestamp;

    /**
     * 启动 禁用
     */
    private Integer  status;

    /**
     * 创建时间
     */

    private LocalDateTime createTime;

    /**
     * 创建时间
     */

    private LocalDateTime updateTime;

    /**
     * 手机号
     */
    @PrivacyEncrypt(type = PrivacyTypeEnum.PHONE)
    private String phone;

    /**
     * 是否管理员
     */
    private Integer  isAdmin;

    /**
     * 错误次数
     */
    private Integer  errorRetry;
}
