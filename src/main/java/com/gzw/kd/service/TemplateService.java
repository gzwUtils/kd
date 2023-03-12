package com.gzw.kd.service;


import com.gzw.kd.common.entity.WeChatTemplateMsg;

import java.util.List;

/**
 * @author gaozhiwei
 */
public interface TemplateService {

    /**
     * 根据role 获取模板信息
     * @param role
     * @return list
     * @throws Exception err
     */

    List<WeChatTemplateMsg> getTemplateByRole(Integer  role)throws  Exception;

}
