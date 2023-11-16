package com.gzw.kd.controller;


import cn.hutool.core.util.ObjectUtil;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.utils.FlowableUtils;
import com.gzw.kd.vo.input.StudentLeaveInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.task.api.Task;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec
 */
@Slf4j
@Api(tags = "flowable")
@RequestMapping("/flow")
@RestController
@SuppressWarnings("all")
public class FlowableController {

    @Resource
    private FlowableUtils flowableUtils;


    /**
     * 发起流程
     *
     * @param processKey
     * @param businessKey
     * @param map
     * @return
     */
    @OperatorLog(value = "发起流程", description = "员工请假")
    @ApiOperation(value = "发起流程")
    @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R start(@RequestBody @Validated StudentLeaveInput input) {
        flowableUtils.startAndComplete(input.getProcessKey(), input.getBusinessKey(), input.getMap());
        return R.ok();
    }


    @OperatorLog(value = "审批", description = "老师审批")
    @ApiOperation(value = "审批")
    @RequestMapping(value = "/approvalOne", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R approvalOne(@RequestBody StudentLeaveInput input) {
        List<Task> list = flowableUtils.getListByGroup(input.getGroup());
        Task task = flowableUtils.getOneByBusinessKey(list, input.getBusinessKey());
        if(ObjectUtil.isEmpty(task)){
            return R.error().message("任务不存在");
        }
        flowableUtils.complete(task.getId(), input.getMap());
        return R.ok().data("processId", task.getProcessInstanceId());
    }


    @OperatorLog(value = "历史流程", description = "获取历史信息")
    @ApiOperation(value = "历史流程")
    @RequestMapping(value = "/history", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R history(@RequestParam("processId") String processInstanceId) {
        List<HistoricActivityInstance> historyList = flowableUtils.getHistoryList(processInstanceId);
        return R.ok().data("history",historyList);
    }


}
