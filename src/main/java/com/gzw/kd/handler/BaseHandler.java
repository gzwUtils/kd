package com.gzw.kd.handler;

import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.entity.SendResult;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.flowControl.FlowControlFactory;
import com.gzw.kd.service.MessageRecordService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author gzw
 * &#064;description：处理器基类
 * @since：2023/5/24 14:52
 */
@Slf4j
public abstract class BaseHandler implements Handler {

    @Resource
    private HandlerHolder handlerHolder;

    @Resource
    private FlowControlFactory flowControlFactory;

    @Resource
    private MessageRecordService messageRecordService;


    /**
     * 标识渠道的Code，子类初始化时指定
     */
    protected Integer channelCode;

    /**
     * 处理器名称
     * -- GETTER --
     *  获取处理器名称

     */
    @Getter
    protected String handlerName;

    /**
     * 限流相关的参数，子类初始化时指定
     */
    protected FlowControlParam flowControlParam;

    /**
     * 是否启用，默认启用
     * -- GETTER --
     *  是否启用

     */
    @Getter
    protected boolean enabled = true;

    @PostConstruct
    public void init() {
        // 设置处理器名称
        handlerName = this.getClass().getSimpleName();

        // 注册处理器
        if (channelCode != null && handlerHolder != null) {
            handlerHolder.putHandler(channelCode, this);
            log.info("处理器注册成功: {} -> {}", channelCode, handlerName);
        } else {
            log.warn("处理器注册失败: channelCode={}, handlerHolder={}",
                    channelCode, handlerHolder);
        }
    }

    /**
     * 流量控制
     */
    protected void flowControl(TaskInfo taskInfo) {
        if (enabled && Objects.nonNull(flowControlParam) && flowControlFactory != null) {
            try {
                flowControlFactory.flowControl(taskInfo, flowControlParam);
                log.debug("流量控制通过: {} - {}", handlerName, taskInfo.getBusinessId());
            } catch (Exception e) {
                log.error("流量控制异常: {} - {}", handlerName, taskInfo.getBusinessId(), e);
                throw e;
            }
        }
    }

    /**
     * 统一处理的handler接口
     */
    @Override
    public SendResult doHandler(TaskInfo taskInfo) {
        SendResult result = new SendResult();
        result.setTaskInfo(taskInfo);
        result.setChannelCode(channelCode);
        result.setHandlerName(handlerName);
        result.setEnabled(enabled);

        long startTime = System.currentTimeMillis();
        // 检查处理器是否启用
        if (!enabled) {
            result.setSuccess(false);
            result.setErrorMsg("处理器已禁用");
            log.warn("处理器已禁用: {} - {}", handlerName, taskInfo.getBusinessId());
            return result;
        }

        try {
            // 流量控制
            flowControl(taskInfo);

            Long recordId = messageRecordService.createRecord(taskInfo);

            taskInfo.setRecordId(recordId);
            // 执行具体处理
            boolean success = handler(taskInfo);

            long executeTime = System.currentTimeMillis() - startTime;

            // 3. 更新记录状态
            if (recordId != null) {
                // 计算成功失败数量（根据具体业务）
                int receiverCount = (taskInfo.getReceiver() != null && !taskInfo.getReceiver().isEmpty())
                        ? taskInfo.getReceiver().size()
                        : 1;

                int successCount = success ? receiverCount : 0;
                int failCount = success ? 0 : receiverCount;

                messageRecordService.updateStatus(recordId, success, successCount, failCount);
            }
            result.setExecuteTime(executeTime);
            result.setSuccess(success);

            if (success) {
                log.info("发送成功 channel:{} handler:{} businessId:{} receiver:{} time:{}ms",
                        channelCode, handlerName, taskInfo.getBusinessId(),
                        taskInfo.getReceiver(), executeTime);
            } else {
                result.setErrorMsg("发送失败");
                log.error("发送失败 channel:{} handler:{} businessId:{} receiver:{} time:{}ms",
                        channelCode, handlerName, taskInfo.getBusinessId(),
                        taskInfo.getReceiver(), executeTime);
            }

        } catch (Exception e) {
            long executeTime = System.currentTimeMillis() - startTime;
            result.setExecuteTime(executeTime);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());

            log.error("发送异常 channel:{} handler:{} businessId:{} receiver:{} error:{} time:{}ms",
                    channelCode, handlerName, taskInfo.getBusinessId(),
                    taskInfo.getReceiver(), e.getMessage(), executeTime, e);
        }

        return result;
    }

    /**
     * 具体处理器实现，由子类实现
     */
    public abstract boolean handler(TaskInfo taskInfo);

    /**
     * 启用处理器
     */
    public void enable() {
        this.enabled = true;
        log.info("处理器已启用: {}", handlerName);
    }

    /**
     * 禁用处理器
     */
    public void disable() {
        this.enabled = false;
        log.info("处理器已禁用: {}", handlerName);
    }

}