package com.gzw.kd.common.enums;

import com.gzw.kd.common.entity.ContentModel;
import com.gzw.kd.common.entity.EmailContentModel;
import com.gzw.kd.common.entity.SmsContentModel;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:41
 */

@SuppressWarnings("all")
@AllArgsConstructor
@Getter
public enum ChannelTypeEnum implements BaseEnum {



    /** sms(短信)*/
    SMS(30, "sms(短信)", SmsContentModel.class, "sms"),



    /** email(邮件) -- QQ、163邮箱*/
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email"),
    ;



    /**
     * 编码值
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 内容模型Class
     */
    private final Class<? extends ContentModel> contentModelClass;

    /**
     * 英文标识
     */
    private final String codeEn;


    public static Class<? extends ContentModel> getContextModel(int code ){
       return Arrays.stream(values()).filter
                (channelTypeEnum -> Objects.equals(code,channelTypeEnum.getCode()))
                .map(ChannelTypeEnum::getContentModelClass).findFirst().orElse(null);
    }


    public static String getDescription(int code) {
        for (ChannelTypeEnum channelTypeEnum : values()) {
            if (channelTypeEnum.getCode().equals(code)) {
                return channelTypeEnum.getDescription();
            }
        }
        return null;
    }

}
