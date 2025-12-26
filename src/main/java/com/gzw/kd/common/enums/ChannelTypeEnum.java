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



    /** im(即时通讯)*/
    IM(10, "im(即时通讯)", ContentModel.class, "im"),

    /** push(推送)*/
    PUSH(20, "push(推送)", ContentModel.class, "push"),


    /** sms(短信)*/
    SMS(30, "sms(短信)", SmsContentModel.class, "sms"),



    /** email(邮件) -- QQ、163邮箱*/
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email"),

    /** 公众号*/
    WECHAT_MP(50, "公众号", ContentModel.class, "wechat_mp"),

    /** 小程序*/
    WECHAT_XCX(60, "小程序", ContentModel.class, "wechat_xcx"),

    /** 企业微信*/
    WECHAT_QYWX(70, "企业微信", ContentModel.class, "wechat_qywx"),

    /** 钉钉机器人*/
    DING_TALK_ROBOT(80, "钉钉机器人", ContentModel.class, "ding_talk_robot"),

    /** 钉钉工作通知*/
    DING_TALK_WORK_NOTICE(90, "钉钉工作通知", ContentModel.class, "ding_talk_work_notice"),

    /** 企业微信机器人*/
    WECHAT_QYWX_ROBOT(100, "企业微信机器人", ContentModel.class, "wechat_qywx_robot"),

    /** 飞书机器人*/
    FEI_SHU_ROBOT(110, "飞书机器人", ContentModel.class, "fei_shu_robot"),

    /** 站内信*/
    IN_SITE_MESSAGE(120, "站内信", ContentModel.class, "in_site_message"),
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
