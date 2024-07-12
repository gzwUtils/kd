package com.gzw.kd.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.gzw.kd.common.R;
import com.gzw.kd.common.XxlJobConstant;
import com.gzw.kd.common.entity.XxlJobGroup;
import com.gzw.kd.common.entity.XxlJobInfo;
import com.gzw.kd.service.CronTaskService;
import com.xxl.job.core.biz.model.ReturnT;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/6/5 16:47
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class CronTaskServiceImpl implements CronTaskService {

    @Value("${xxl.job.admin.username:admin}")
    private String xxlUserName;

    @Value("${xxl.job.admin.password:123456}")
    private String xxlPassword;

    @Value("${xxl.job.admin.addresses}")
    private String xxlAddresses;


    @Override
    public R saveCronTask(XxlJobInfo xxlJobInfo) {
        String path = Objects.isNull(xxlJobInfo.getId()) ? xxlAddresses + XxlJobConstant.INSERT_URL
                : xxlAddresses + XxlJobConstant.UPDATE_URL;
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                if (path.contains(XxlJobConstant.INSERT_URL)) {
                    Integer taskId = Integer.parseInt(String.valueOf(returnT.getContent()));
                    return R.ok().data("taskId",taskId);
                } else {
                    return R.ok();
                }
            }
        } catch (Exception e) {
            log.error("save xxl error param{}", JSON.toJSONString(xxlJobInfo), e);
        }
        return R.error().message("定时任务保存失败");
    }

    @Override
    public R deleteCronTask(Integer taskId) {
        String path = xxlAddresses + XxlJobConstant.DELETE_URL;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);
        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return R.ok();
            }
        } catch (Exception e) {
            log.error("delete xxl error taskId{}", taskId, e);
        }
        return R.error().message("定时任务删除失败");
    }

    @Override
    public R startCronTask(Integer taskId) {
        String path = xxlAddresses + XxlJobConstant.RUN_URL;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);
        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return R.ok();
            }
        } catch (Exception e) {
            log.error("startCronTask xxl error taskId{}", taskId, e);
        }
        return R.error().message("定时任务开启失败");
    }

    @Override
    public R stopCronTask(Integer taskId) {
        String path = xxlAddresses + XxlJobConstant.STOP_URL;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);
        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return R.ok();
            }
        } catch (Exception e) {
            log.error("stopCronTask xxl error taskId{}", taskId, e);
        }
        return R.error().message("定时任务停用失败");
    }

    @Override
    public Integer getGroupId(String appName, String title) {
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_PAGE_LIST;
        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("appname", appName);
        params.put("title", title);
        HttpResponse response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
        return JSON.parseObject(response.body()).getJSONArray("data").getJSONObject(0).getInteger("id");
    }

    @Override
    public R createGroup(XxlJobGroup xxlJobGroup) {
        Map params = JSON.parseObject(JSON.toJSONString(xxlJobGroup), Map.class);
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_INSERT_URL;
        HttpResponse response = null;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return R.ok();
            }
        } catch (Exception e) {
            log.error("stopCronTask xxl error xxlJobGroup{}", xxlJobGroup, e);
        }
        return R.error().message("createGroup失败");
    }


    /**
     * 获取xxl cookie
     *
     * @return String
     */
    private String getCookie() {
        Map<String, Object> params = MapUtil.newHashMap();
        params.put("userName", xxlUserName);
        params.put("password", xxlPassword);
        params.put("randomCode", IdUtil.fastSimpleUUID());

        String path = xxlAddresses + XxlJobConstant.LOGIN_URL;
        HttpResponse response = null;
        try {
            response = HttpRequest.post(path).form(params).execute();
            if (response.isOk()) {
                List<HttpCookie> cookies = response.getCookies();
                StringBuilder sb = new StringBuilder();
                for (HttpCookie cookie : cookies) {
                    sb.append(cookie.toString());
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup getCookie,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(response));
        }
        return null;
    }
}
