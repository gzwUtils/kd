package com.gzw.kd.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.gzw.kd.common.entity.AsyncTasksEntity;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.enums.AsyncTaskStatusEnum;
import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.export.AsyncTaskLogService;
import com.gzw.kd.mapper.AsyncTasksLogMapper;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;
import static com.gzw.kd.common.Constants.*;

/**
 * @author 高志伟
 */
@Slf4j
@Service
public class AsyncTaskLogServiceImpl implements AsyncTaskLogService {


    @Resource
    AsyncTasksLogMapper asyncTasksLogMapper;

    public AsyncTaskLogServiceImpl(AsyncTasksLogMapper asyncTasksLogMapper) {
        this.asyncTasksLogMapper = asyncTasksLogMapper;
    }

    @Override
    public Long addOne(Object paramsObject, AsyncTaskTypeEnum taskType) {
        Operator account = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        LocalDateTime now = LocalDateTime.now();
        AsyncTasksEntity asyncTasksEntity = new AsyncTasksEntity()
                .setParams(JSON.toJSONString(paramsObject))
                .setStatus(AsyncTaskStatusEnum.UNTREATED.getCode())
                .setExportName(account.getAccount() + STRING_UNDERLINE
                        + now.format(DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN)))
                .setType(taskType.getCode())
                .setCreator(account.getAccount())
                .setCreateTime(now)
                .setUpdater(account.getAccount())
                .setUpdateTime(now);
        asyncTasksLogMapper.insertOne(asyncTasksEntity);
        return asyncTasksEntity.getId();
    }

    @Override
    public Long addOne(Object paramsObject, AsyncTaskTypeEnum taskType, String exportName, LocalDateTime time) {
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        String account = ObjectUtil.isNotEmpty(operator) && StringUtils.isNotBlank(operator.getAccount())?operator.getAccount():"other";
        AsyncTasksEntity asyncTasksEntity = new AsyncTasksEntity()
                .setParams(JSON.toJSONString(paramsObject))
                .setStatus(AsyncTaskStatusEnum.UNTREATED.getCode())
                .setExportName(exportName)
                .setType(taskType.getCode())
                .setCreator(account)
                .setCreateTime(time)
                .setUpdater(account).setUpdateTime(time);
        asyncTasksLogMapper.insertOne(asyncTasksEntity);
        return asyncTasksEntity.getId();
    }

    @Override
    public void updateOne(Long id, AsyncTaskStatusEnum taskStatus, AsyncTaskOutput asyncTaskOutput, String exception, String filePath) {
        if (null == id || id < INT_ONE) {
            throw new IllegalArgumentException("Id can not be null or less than 1");
        }
        AsyncTasksEntity asyncTasksEntity = new AsyncTasksEntity()
                .setId(id).setStatus(taskStatus.getCode()).setErrorMsg(exception);
        if (null != asyncTaskOutput) {
            asyncTasksEntity
                    .setExportName(asyncTaskOutput.getFileName())
                    .setNumberOfSuccesses(asyncTaskOutput.getNumberOfSuccesses())
                    .setNumberOfFailed(asyncTaskOutput.getNumberOfFailed()).setFilePath(filePath);
        }
        asyncTasksLogMapper.updateOne(asyncTasksEntity);
    }

    @Override
    public List<AsyncTasksEntity> fetchUnCompletedTasks(int size) {
        return asyncTasksLogMapper.selectAll(AsyncTaskStatusEnum.UNTREATED.getCode(),size);
    }
}
