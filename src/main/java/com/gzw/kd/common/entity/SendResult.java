package com.gzw.kd.common.entity;
import lombok.Data;

/**
 * 发送结果
 */
@Data
public class SendResult {
    /**
     * 任务信息
     */
    private TaskInfo taskInfo;

    /**
     * 渠道编码
     */
    private Integer channelCode;

    /**
     * 处理器名称
     */
    private String handlerName;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行耗时(毫秒)
     */
    private Long executeTime;

    /**
     * 处理器是否启用
     */
    private boolean enabled;

    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();

}