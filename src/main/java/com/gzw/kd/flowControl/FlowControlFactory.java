package com.gzw.kd.flowControl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import static com.gzw.kd.common.Constants.FLOW_CONTROL_PREFIX;
import com.gzw.kd.common.annotation.LocalRateLimit;
import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.RateLimitStrategy;
import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.flowControl.service.FlowControlService;
import com.gzw.kd.common.entity.TaskInfo;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 10:41
 */
@SuppressWarnings("all")
@Slf4j
@Component
@DependsOn({"applicationContextUtils"})
public class FlowControlFactory {


    @Value("${flowControl}")
    private String flowControl;

    private final Map<RateLimitStrategy, FlowControlService> flowControlServiceMap = new ConcurrentHashMap<>();


    public void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        Double rateInitValue = flowControlParam.getRateInitValue();
        // 对比 初始限流值 与 配置限流值，以 配置中心的限流值为准
        Double rateLimitConfig = getRateLimitConfig(taskInfo.getSendChannel());
        if (Objects.nonNull(rateLimitConfig) && !rateInitValue.equals(rateLimitConfig)) {
            RateLimiter rateLimiter = RateLimiter.create(rateLimitConfig);
            flowControlParam.setRateInitValue(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter);
        }
        FlowControlService flowControlService = flowControlServiceMap.get(flowControlParam.getRateLimitStrategy());
        if (Objects.isNull(flowControlService)) {
            log.error("没有找到对应的单机限流策略.......................");
            return;
        }
        double costTime = flowControlService.flowControl(taskInfo, flowControlParam);
        if (costTime > 0) {
            log.info("consumer {} flow control time {}", ChannelTypeEnum.getDescription(taskInfo.getSendChannel()), costTime);
        }
    }

    /**
     * 得到限流值的配置
     *
     * @param channelCode 渠道code
     */
    private Double getRateLimitConfig(Integer channelCode) {
        JSONObject jsonObject = JSON.parseObject(flowControl);
        if (Objects.isNull(jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode))) {
            return null;
        }
        return jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode);
    }

    @PostConstruct
    private void init() {
        log.info("flow control init start.........");
        Map<String, Object> serviceMap = ApplicationContextUtils.getApplicationContext().getBeansWithAnnotation(LocalRateLimit.class);
        serviceMap.forEach((name, service) -> {
            if (service instanceof FlowControlService) {
                LocalRateLimit localRateLimit = AopUtils.getTargetClass(service).getAnnotation(LocalRateLimit.class);
                RateLimitStrategy rateLimitStrategy = localRateLimit.rateLimitStrategy();
                //通常情况下 实现的限流service与rateLimitStrategy一一对应
                flowControlServiceMap.put(rateLimitStrategy, (FlowControlService) service);
            }
        });
        log.info("flow control init end.........");
    }
}
