package com.gzw.kd.common.entity;
import com.alibaba.fastjson2.annotation.JSONField;
import com.gzw.kd.common.annotation.PrivacyEncrypt;
import com.gzw.kd.common.enums.PrivacyTypeEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： 日志
 * @since：2023/2/8 14:14
 */

@Data
@Accessors(chain = true)
public class Log  {

    @JSONField(name = "username")
    @PrivacyEncrypt(type = PrivacyTypeEnum.CUSTOMER,prefixNoMaskLen = 2,suffixNoMaskLen = 0,symbol = "h")
    private String userName;

    private String operation;

    private String desc;

    private String ip;

    @JSONField(name = "create_time")
    private LocalDateTime createTime;

    private String location;

    private String result;

    private int id ;

    private int time;
}
