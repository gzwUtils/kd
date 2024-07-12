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

    @Override
    public WeChatTemplateMsg getTemplateById(Integer id) {
        return templateMapper.getTemplateById(id);
    }

    @Override
    public List<WeChatTemplateMsg> getTemplateBySys(String sys) {
        return templateMapper.getTemplateBySys(sys);
    }

    @Override
    public List<String> getAllSys() {
        return templateMapper.getAllSys();
    }

    @Override
    public WeChatTemplateMsg getTemplateByName(String name) {
        return templateMapper.getTemplateByName(name);
    }
}
