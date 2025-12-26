package com.gzw.kd.common.entity;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import static com.gzw.kd.common.Constants.STRING_ZERO;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskInfo {

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 业务Id(数据追踪使用)
     * 生成逻辑参考 TaskInfoUtils
     */
    private Long businessId;

    /**
     * 接收者
     */
    private Set<String> receiver;

    /**
     * 发送的Id类型
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 发送文案模型
     * template info 表存储的content是JSON(所有内容都会塞进去)
     * 不同的渠道要发送的内容不一样
     * 所以会有ContentModel
     */
    private String contentModel;

    /**
     * 发送账号（邮件下可有多个发送账号、短信可有多个发送账号..）
     */
    private String sendAccount;


    // 延迟相关
    private Long delayMinutes = 0L;    // 延迟分钟数
    private Boolean isDelayed = false; // 是否延迟

    // 消费端使用
    private Long recordId;


    /**
     * 解析expectPushTime参数
     */
    public void parseExpectPushTime(String expectPushTime ) {
        if (STRING_ZERO.equals(expectPushTime)) {
            // 立即发送
            this.delayMinutes = 0L;
            this.isDelayed = false;
        } else if (StringUtils.isNumeric(expectPushTime)) {
            // 延迟发送（分钟数）
            this.delayMinutes = Long.parseLong(expectPushTime);
            this.isDelayed = true;
        } else {
            // 定时任务cron表达式
            // 定时任务cron表达式
            try {
                // 验证cron表达式是否有效
                CronExpression cronExpression = new CronExpression(expectPushTime);

                // 获取最近一次的触发时间
                Date now = new Date();
                Date nextExecuteTime = cronExpression.getNextValidTimeAfter(now);

                if (nextExecuteTime == null) {
                    throw new IllegalArgumentException("无法计算下一次执行时间");
                }

                // 计算从当前时间到下次执行时间的分钟数差
                long diffInMillis = nextExecuteTime.getTime() - now.getTime();
                this.delayMinutes = diffInMillis / (1000 * 60);

                // 如果延迟分钟数大于0，则标记为延迟
                this.isDelayed = this.delayMinutes > 0;

            } catch (ParseException e) {
                throw new IllegalArgumentException("无效的cron表达式: " + expectPushTime);
            }
        }
    }

}
