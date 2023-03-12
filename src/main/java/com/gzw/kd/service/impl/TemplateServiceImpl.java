package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.WeChatTemplateMsg;
import com.gzw.kd.mapper.TemplateMapper;
import com.gzw.kd.service.TemplateService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;


/**
 * @author 高志伟
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;




    @Override
    public List<WeChatTemplateMsg> getTemplateByRole(Integer role) throws Exception {
        return templateMapper.getTemplateByRole(role);
    }
}
