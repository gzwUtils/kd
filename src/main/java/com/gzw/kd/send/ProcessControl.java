package com.gzw.kd.send;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 流程控制器
 * @since：2023/5/25 23:37
 */
@Data
@Slf4j
@SuppressWarnings("all")
public class ProcessControl {

    /**
     * 模板映射
     */
    private  Map<String, ProcessTemplate> templateConfig = null;


    /**
     * 执行责任链
     *
     * @param context
     * @return 返回上下文内容
     */
    public ProcessContext process(ProcessContext context) {

        /**
         * 前置检查
         */
        try {
            preCheck(context);
        } catch (GlobalException e) {
            log.error("执行 process error {}",e.getMessage(),e);
        }

        /**
         * 遍历流程节点
         */
        List<BusinessProcess> processList = templateConfig.get(context.getCode()).getProcessList();
        for (BusinessProcess businessProcess : processList) {
            businessProcess.process(context);
            if (context.getNeedBreak()) {
                break;
            }
        }
        return context;
    }


    /**
     * 执行前检查，出错则抛出异常
     *
     * @param context 执行上下文
     * @throws ProcessException 异常信息
     */
    private void preCheck(ProcessContext context) throws GlobalException {
        // 上下文
        if (Objects.isNull(context)) {
            context = new ProcessContext();
            context.setResponse(R.setResult((ResultCodeEnum.NULL_POINT))).setNeedBreak(true);
            throw new GlobalException(JSON.toJSONString(context),ResultCodeEnum.NULL_POINT.getCode());
        }

        // 业务代码
        String businessCode = context.getCode();
        if (StrUtil.isBlank(businessCode)) {
            context.setResponse(R.setResult((ResultCodeEnum.PARAM_ABSENT))).setNeedBreak(true);
            throw new GlobalException(JSON.toJSONString(context),ResultCodeEnum.PARAM_ABSENT.getCode());
        }

        // 执行模板
        ProcessTemplate processTemplate = templateConfig.get(businessCode);
        if (Objects.isNull(processTemplate)) {
            context.setResponse(R.setResult((ResultCodeEnum.PARAM_ABSENT))).setNeedBreak(true);
            throw new GlobalException(JSON.toJSONString(context),ResultCodeEnum.PARAM_ABSENT.getCode());
        }

        // 执行模板列表
        List<BusinessProcess> processList = processTemplate.getProcessList();
        if (CollUtil.isEmpty(processList)) {
            context.setResponse(R.setResult((ResultCodeEnum.PARAM_ABSENT))).setNeedBreak(true);
            throw new GlobalException(JSON.toJSONString(context),ResultCodeEnum.PARAM_ABSENT.getCode());
        }

    }
}
