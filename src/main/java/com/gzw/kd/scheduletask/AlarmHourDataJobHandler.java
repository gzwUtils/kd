package com.gzw.kd.scheduletask;

import com.gzw.kd.common.utils.XxlJobLogUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 告警
 * @since：2023/6/4 15:02
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class AlarmHourDataJobHandler {

    @XxlJob("AlarmHourDataJobHandler")
    public ReturnT<String> execute(){
        String param = XxlJobHelper.getJobParam();
        XxlJobLogUtil.log(log,false,"告警消息开始推送... param[{}]",param);
        return ReturnT.SUCCESS;
    }
}
