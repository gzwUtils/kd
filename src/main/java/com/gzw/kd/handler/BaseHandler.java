package com.gzw.kd.handler;

import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.flowControl.FlowControlFactory;
import com.gzw.kd.common.entity.TaskInfo;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:52
 */
@AllArgsConstructor
@Slf4j
public abstract class BaseHandler implements Handler{

    private final HandlerHolder handlerHolder;

    private final FlowControlFactory flowControlFactory;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer channelCode;

    /**
     * 限流相关的参数
     * 子类初始化的时候指定
     */
    protected FlowControlParam flowControlParam;


    @PostConstruct
    public void init(){
        handlerHolder.putHandler(channelCode,this);
    }

    /**
     * 流量控制
     *
     * @param taskInfo info
     */
    public void flowControl(TaskInfo taskInfo) {
        // 只有子类指定了限流参数，才需要限流
        if (Objects.nonNull(flowControlParam)) {
            flowControlFactory.flowControl(taskInfo, flowControlParam);
        }
    }


    @Override
    public void doHandler(TaskInfo taskInfo) {
        flowControl(taskInfo);
        if (handler(taskInfo)) {
            log.info("send success channelCode:{} businessId:{},receiver:{}", channelCode, taskInfo.getBusinessId(), taskInfo.getReceiver());
        } else {
            log.error("send error channelCode:{} businessId:{},receiver:{}", channelCode, taskInfo.getBusinessId(), taskInfo.getReceiver());
        }
    }




    /**
     * 统一处理的handler接口
     *
     * @param taskInfo task
     * @return flag
     */
    public abstract boolean handler(TaskInfo taskInfo);
}
