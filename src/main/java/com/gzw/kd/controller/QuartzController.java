package com.gzw.kd.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.utils.QuartzManager;
import com.gzw.kd.common.entity.JobAndTrigger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *  定时任务
 * @author 高志伟
 */
@Api(tags = "定时任务")
@SuppressWarnings("all")
@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzController {


    @Resource
    private QuartzManager quartzManager;

    /**
     * 新增定时任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @param cron   cron表达式
     * @param cron   job全类名称
     * @return ResultMap
     */
    @OperatorLog(value = "新增定时任务",description = "新增定时任务")
    @ApiImplicitParams({@ApiImplicitParam(name = "jName", value = "任务名称", paramType = "query", dataTypeClass = String.class, required = true ),
            @ApiImplicitParam(name = "jGroup", value = "任务组", paramType = "query", dataTypeClass = String.class, required = true ),
            @ApiImplicitParam(name = "cron", value = "cron表达式", paramType = "query", dataTypeClass = String.class, required = true ),
            @ApiImplicitParam(name = "jobClassName", value = "job全类名称", paramType = "query", dataTypeClass = String.class, required = true )})
    @ApiOperation(value = "新增定时任务")
    @PostMapping(path = "/addJob")
    public R addJob(@RequestParam("jName") String jName, @RequestParam("jGroup") String jGroup,@RequestParam("cron") String cron,@RequestParam("jobClassName") String jobClassName) {
        try {
            quartzManager.getInstance().addJob(jName, jGroup, jName, jGroup, cron, jobClassName);
            return R.ok().message("添加任务成功");
        } catch (Exception e) {
            log.error("add job error jName {}", jName, e);
            return R.error().message("添加任务失败");
        }
    }

    /**
     * 暂停任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @return ResultMap
     */
    @OperatorLog(value = "暂停任务",description = "暂停任务")
    @ApiOperation(value = "暂停任务")
    @PostMapping(path = "/pauseJob")
    public R pauseJob(String jName, String jGroup) {
        try {
            quartzManager.getInstance().pauseJob(jName, jGroup);
            return R.ok().message("暂停任务成功");
        } catch (SchedulerException e) {
            log.error("pause Job error jName {}", jName, e);
            return R.error().message("暂停任务失败");
        }
    }

    /**
     * 恢复任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @return ResultMap
     */
    @ApiOperation(value = "恢复任务")
    @OperatorLog(value = "恢复任务",description = "恢复任务")
    @PostMapping(path = "/resumeJob")
    public R resumeJob(String jName, String jGroup) {
        try {
            quartzManager.getInstance().resumeJob(jName, jGroup);
            return R.ok().message("恢复任务成功");
        } catch (SchedulerException e) {
            log.error("resume Job error jName {}", jName, e);
            return R.error().message("恢复任务失败");
        }
    }

    /**
     * 重启任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @param cron   cron表达式
     * @return ResultMap
     */
    @ApiOperation(value = "重启任务")
    @OperatorLog(value = "重启任务",description = "重启任务")
    @PostMapping(path = "/rescheduleJob")
    public R rescheduleJob(String jName, String jGroup, String cron) {
        try {
            quartzManager.getInstance().rescheduleJob(jName, jGroup, cron);
            return R.ok().message("重启任务成功");
        } catch (SchedulerException e) {
            log.error("reschedule Job error jName {}", jName, e);
            return R.error().message("重启任务失败");
        }
    }

    /**
     * 删除任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @return ResultMap
     */
    @ApiOperation(value = "删除任务")
    @OperatorLog(value = "删除任务",description = "删除任务")
    @PostMapping(path = "/deleteJob")
    public R deleteJob(String jName, String jGroup) {
        try {
            quartzManager.getInstance().deleteJob(jName, jGroup);
            return R.ok().message("删除任务成功");
        } catch (SchedulerException e) {
            log.error("delete Job error jName {}", jName, e);
            return R.error().message("删除任务失败");
        }
    }

    /**
     * 查询任务
     *
     * @param pageNum  页码
     * @param pageSize 每页显示多少条数据
     * @return Map
     */
    @ApiOperation(value = "查询任务")
    @OperatorLog(value = "查询任务",description = "查询任务")
    @GetMapping(path = "/queryJob")
    public R queryJob(Integer pageNum, Integer pageSize) {
        PageInfo<JobAndTrigger> pageInfo = quartzManager.getInstance().getJobAndTriggerDetails(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(pageInfo.getTotal())) {
            map.put("jobs", pageInfo.getList());
            map.put("total", pageInfo.getTotal());
            return R.ok().data(map).message("查询任务成功");
        }
        return R.error().message("查询任务成功失败，没有数据");
    }
}
