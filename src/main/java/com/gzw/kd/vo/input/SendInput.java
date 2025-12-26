package com.gzw.kd.vo.input;

import com.gzw.kd.common.entity.MessageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * @author gzw
 * @description：
 * @since：2023/5/25 23:55
 */
@Data
@Accessors(chain = true)
public class SendInput {

    /**
     * 执行业务类型
     * send:发送消息
     * recall:撤回消息
     */
    private String code;

    /**
     * 消息模板Id
     * 【必填】
     */
    private Long messageTemplateId;



    /**
     * 推送消息的时间
     * 0：立即发送 30：30分钟后发送
     * else：crontab 表达式
     */
    @ApiModelProperty(value = "推送消息的时间 0 立即发送,crontab")
    private String expectPushTime ;


    /**
     * 消息相关的参数
     * 当业务类型为"send"，必传
     */
    private MessageParam messageParam;
}
