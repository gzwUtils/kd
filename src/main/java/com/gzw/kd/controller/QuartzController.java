package com.gzw.kd.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.gzw.kd.common.R;
import com.gzw.kd.common.utils.QuartzManager;
import com.gzw.kd.common.entity.JobAndTrigger;
import com.gzw.kd.scheduletask.HelloJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *  定时任务
 * @author 高志伟
 */
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
     * @param tName  触发器名称
     * @param tGroup 触发器组
     * @param cron   cron表达式
     * @return ResultMap
     */
    @PostMapping(path = "/addJob")
    public R addJob(String jName, String jGroup, String tName, String tGroup, String cron) {
        try {
            quartzManager.getInstance().addJob(jName, jGroup, tName, tGroup, cron, HelloJob.class);
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
    @GetMapping(path = "/queryJob")
    public R queryJob(Integer pageNum, Integer pageSize) {
        PageInfo<JobAndTrigger> pageInfo = quartzManager.getInstance().getJobAndTriggerDetails(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(pageInfo.getTotal())) {
            map.put("JobAndTrigger", pageInfo);
            map.put("number", pageInfo.getTotal());
            return R.ok().data(map).message("查询任务成功");
        }
        return R.error().message("查询任务成功失败，没有数据");
    }
}
