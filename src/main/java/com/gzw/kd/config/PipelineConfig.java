package com.gzw.kd.config;

import com.gzw.kd.common.enums.BusinessCode;
import com.gzw.kd.send.ProcessControl;
import com.gzw.kd.send.ProcessTemplate;
import com.gzw.kd.send.action.AfterParamCheckAction;
import com.gzw.kd.send.action.AssembleAction;
import com.gzw.kd.send.action.PreParamCheckAction;
import com.gzw.kd.send.action.SendMqAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 23:27
 */

@Configuration
public class PipelineConfig {

    @Resource
    private SendMqAction sendMqAction;

    @Resource
    private AssembleAction assembleAction;

    @Resource
    private PreParamCheckAction preParamCheckAction;

    @Resource
    private AfterParamCheckAction afterParamCheckAction;

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     *
     */
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(preParamCheckAction,assembleAction,afterParamCheckAction, sendMqAction));
        return processTemplate;
    }

    /**
     * 消息撤回执行流程
     * 1.组装参数
     * 2.发送MQ
     *
     * @return 执行结果
     */
    @Bean("recallMessageTemplate")
    public ProcessTemplate recallMessageTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(assembleAction, sendMqAction));
        return processTemplate;
    }

    /**
     * pipeline流程控制器
     *
     * @return 初始化定义
     */
    @Bean
    public ProcessControl processController() {
        ProcessControl processControl = new ProcessControl();
        Map<String, ProcessTemplate> templateConfig = new HashMap<>(4);
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        templateConfig.put(BusinessCode.RECALL.getCode(), recallMessageTemplate());
        processControl.setTemplateConfig(templateConfig);
        return processControl;
    }
}
