package com.gzw.kd.service.impl;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.enums.TemplateStatusEnum;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.mapper.MessageTemplateMapper;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.service.MessageTemplateService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 23:10
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Resource
    MessageTemplateMapper messageTemplateMapper;

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
    public Integer registerTemplate(TemplateInfo templateInfo) {
        long millis = System.currentTimeMillis();
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        TemplateInfo info = new TemplateInfo();
        BeanUtils.copyProperties(templateInfo,info);
        info.setCreator(operator.getAccount()).setCreated((int) millis).setIsDeleted(TemplateStatusEnum.START.getStatus());
        return messageTemplateMapper.registerTemplate(info);
    }
}
