package com.gzw.kd.common.utils;

import com.gzw.kd.common.enums.DataSourceEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.config.datasource.annotation.SpecDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：flowable工具
 */
@SpecDataSource(DataSourceEnum.FLOWABLE)
@Slf4j
@Component
@SuppressWarnings("all")
public class FlowableUtils {


    /**
     * 流程运行控制服务
     */
    private final RuntimeService runtimeService;

    /**
     * 任务管理服务
     */
    private final TaskService taskService;

    /**
     * 流程引擎
     */
    private final ProcessEngine processEngine;

    /**
     * 历史任务
     */

    private final HistoryService historyService;


    /**
     * 初始化获取实例
     */
    public FlowableUtils() {
        runtimeService = ApplicationContextUtils.getBean(RuntimeService.class);
        taskService = ApplicationContextUtils.getBean(TaskService.class);
        historyService = ApplicationContextUtils.getBean(HistoryService.class);
        processEngine = ApplicationContextUtils.getBean(ProcessEngine.class);
    }

    /**
     * 启动流程
     *
     * @param processKey  流程定义key(流程图ID)
     * @param businessKey 业务key
     * @param map         参数键值对
     * @return 流程实例ID
     */
    public String start(String processKey, String businessKey, Map<String, Object> map) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey, map);
        return processInstance.getId();
    }

    /**
     * 终止流程
     *
     * @param processInstanceId 流程实例ID
     * @param reason            终止理由
     */
    public void stop(String processInstanceId, String reason) {

        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }


    /**
     * 获取指定用户的任务列表（创建时间倒序）
     * @param userId 用户ID
     * @return 任务列表
     */
    public List<Task> getListByUserId(String userId) {

        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        return tasks;
    }

    /**
     * 获取指定用户组的任务列表
     * @param group 用户组
     * @return 任务列表
     */
    public List<Task> getListByGroup(String group) {

        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(group).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            log.debug("task processInstanceId : {},id {}",task.getProcessInstanceId(),task.getId());
        }
        return tasks;
    }


    /**
     * 完成指定任务
     * @param taskId 任务ID
     * @param map    变量键值对
     */
    public void complete(String taskId, Map<String, Object> map) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new GlobalException("流程不存在", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
        taskService.complete(taskId, map);
    }

    /**
     * 获取指定任务列表中的特定任务
     * @param list        任务列表
     * @param businessKey 业务key
     * @return 任务
     */
    public Task getOneByBusinessKey(@NotNull List<Task> list, String businessKey) {

        Task task = null;
        for (Task t : list) {
            // 通过任务对象获取流程实例
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
            if (businessKey.equals(pi.getBusinessKey())) {
                task = t;
            }
        }
        return task;
    }

    /**
     * 创建流程并完成第一个任务
     * @param processKey  流程定义key(流程图ID)
     * @param businessKey 业务key
     * @param map         变量键值对
     */
    public void startAndComplete(String processKey, String businessKey, Map<String, Object> map) {

        String processInstanceId = start(processKey, businessKey, map);
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).singleResult();
        taskService.complete(task.getId(), map);

    }

    /**
     * 退回到指定任务节点
     * @param currentTaskId 当前任务ID
     * @param targetTaskKey 目标任务节点key
     */
    public void backToStep(String currentTaskId, String targetTaskKey) {

        Task currentTask = taskService.createTaskQuery().taskId(currentTaskId).singleResult();
        if (currentTask == null) {
            throw new GlobalException("当前任务节点不存在", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
        List<String> currentTaskKeys = new ArrayList<>();
        currentTaskKeys.add(currentTask.getTaskDefinitionKey());
        runtimeService.createChangeActivityStateBuilder().processInstanceId(currentTask.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentTaskKeys, targetTaskKey);
    }

    /**
     * 获取指定流程实例的历史信息
     * @param processInstanceId  流程实例ID
     * @return
     */

    public List<HistoricActivityInstance>  getHistoryList(String processInstanceId){
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();

        return activities;
    }
}
