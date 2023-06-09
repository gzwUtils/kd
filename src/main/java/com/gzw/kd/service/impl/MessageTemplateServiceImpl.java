package com.gzw.kd.service.impl;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.R;
import com.gzw.kd.common.XxlJobConstant;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.entity.XxlJobInfo;
import com.gzw.kd.common.enums.MessageStatusEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.enums.TemplateStatusEnum;
import com.gzw.kd.common.enums.TemplateType;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.common.utils.XxlJobUtils;
import com.gzw.kd.mapper.MessageTemplateMapper;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.service.CronTaskService;
import com.gzw.kd.service.MessageTemplateService;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 23:10
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Resource
    MessageTemplateMapper messageTemplateMapper;

    @Resource
    private CronTaskService cronTaskService;

    @Resource
    private XxlJobUtils xxlJobUtils;

    @Override
    public List<TemplateInfo> findAllByIsDeleted(Integer deleted) {
        return messageTemplateMapper.findAllByIsDeleted(deleted);
    }

    @Override
    public Long countByIsDeletedEquals(Integer deleted) {
        return messageTemplateMapper.countByIsDeletedEquals(deleted);
    }

    @Override
    public TemplateInfo selectById(Integer id) {
        return messageTemplateMapper.selectById(id);
    }

    @Override
    public void registerTemplate(TemplateInfo templateInfo) {
        long millis = System.currentTimeMillis();
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        TemplateInfo info = new TemplateInfo();
        BeanUtils.copyProperties(templateInfo, info);
        if(ObjectUtil.isNotEmpty(templateInfo.getId())){
            resetStatus(templateInfo);
        } else {
            info.setCreator(operator.getAccount()).setCreated((int) millis).setIsDeleted(TemplateStatusEnum.START.getStatus());
            messageTemplateMapper.registerTemplate(info);
        }
    }

    @Override
    public void copy(Long id) {
        TemplateInfo info = messageTemplateMapper.selectById(Math.toIntExact(id));
        if (Objects.nonNull(info)) {
            TemplateInfo clone = ObjectUtil.clone(info).setId(null).setCronTaskId(null);
            Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
            clone.setCreator(operator.getAccount()).setCreated((int) System.currentTimeMillis());
            messageTemplateMapper.registerTemplate(clone);
        }
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        List<TemplateInfo> messageTemplates = messageTemplateMapper.findAllByIds(ids);
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        messageTemplates.stream().filter(p->p.getIsDeleted().
                equals(TemplateStatusEnum.START.getStatus())).forEach(p->{
                    if(ObjectUtil.isNotEmpty(p.getCronTaskId())){
                        cronTaskService.deleteCronTask(p.getCronTaskId());
                    }
                    p.setIsDeleted(TemplateStatusEnum.STOP.getStatus()).setUpdator(operator.getAccount()).setUpdated((int) System.currentTimeMillis());
                }
        );
        messageTemplateMapper.deleteTemplateInfo(messageTemplates);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public R startCronTask(Long id,String executorHandlerName,String desc) {
        // 1.获取消息模板的信息
        TemplateInfo templateInfo = messageTemplateMapper.selectById(Math.toIntExact(id));
        if (Objects.isNull(templateInfo) || TemplateStatusEnum.STOP.getStatus().equals(templateInfo.getIsDeleted())) {
            return R.error().message("模版不存在");
        }

        // 2.动态创建或更新定时任务
        XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(templateInfo,executorHandlerName,desc);

        // 3.获取taskId(如果本身存在则复用原有任务，如果不存在则得到新建后任务ID)
        Integer taskId = templateInfo.getCronTaskId();
        R cronTask = cronTaskService.saveCronTask(xxlJobInfo);
        if (Objects.isNull(taskId) && ResultCodeEnum.SUCCESS.getCode().equals(cronTask.getCode()) && ObjectUtil.isNotEmpty(cronTask.getData())) {
                taskId = Integer.valueOf(String.valueOf(cronTask.getData().get("taskId")));
        }

        // 4. 启动定时任务
        if (Objects.nonNull(taskId)) {
            cronTaskService.startCronTask(taskId);
            Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
            if(ObjectUtil.isNotEmpty(operator)){
                TemplateInfo clone = ObjectUtil.clone(templateInfo).setMsgStatus(MessageStatusEnum.RUN.getCode()).setCronTaskId(taskId).setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
                clone.setCreator(operator.getAccount()).setCreated((int) System.currentTimeMillis());
                messageTemplateMapper.registerTemplate(clone);
                return R.ok();
            }
        }
        return R.error();
    }

    @Override
    public R stopCronTask(Long id) {
        TemplateInfo info = messageTemplateMapper.selectById(Math.toIntExact(id));
        if(ObjectUtil.isEmpty(info) || TemplateStatusEnum.STOP.getStatus().equals(info.getIsDeleted())){
            return R.error().message("模版不存在");
        }
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        TemplateInfo clone = ObjectUtil.clone(info).setMsgStatus(MessageStatusEnum.STOP.getCode()).setUpdated(Math.toIntExact(DateUtil.currentSeconds())).setUpdator(operator.getAccount());
        messageTemplateMapper.updateTemplateInfo(clone);
        cronTaskService.stopCronTask(info.getCronTaskId());
        return R.ok();
    }


    /**
     * 1. 重置模板的状态
     * 2. 修改定时任务信息(如果存在)
     *
     * @param templateInfo info
     */
    private void resetStatus(TemplateInfo templateInfo) {
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        templateInfo.setMsgStatus(MessageStatusEnum.INIT.getCode()).setUpdator(operator.getAccount()).setUpdated(Math.toIntExact(DateUtil.currentSeconds()));

        if (Objects.nonNull(templateInfo.getCronTaskId()) && TemplateType.CLOCKING.getCode().equals(templateInfo.getTemplateType())) {
            XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(templateInfo, XxlJobConstant.EXECUTE_HANDLER_NAME,XxlJobConstant.DESC);
            cronTaskService.saveCronTask(xxlJobInfo);
            cronTaskService.stopCronTask(templateInfo.getCronTaskId());
        }
    }
}
