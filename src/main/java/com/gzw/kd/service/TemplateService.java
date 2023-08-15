package com.gzw.kd.service;


import com.gzw.kd.common.entity.WeChatTemplateMsg;

import java.util.List;

/**
 * @author gaozhiwei
 */
public interface TemplateService {

    /**
     * 根据role 获取模板信息
     * @param role role
     * @return list
     * @throws Exception err
     */

    List<WeChatTemplateMsg> getTemplateByRole(Integer  role)throws  Exception;


    /**
     *  根据模版ID 获取模版信息
     * @param id id
     * @return temp
     */
    WeChatTemplateMsg getTemplateById(Integer id);


    /**
     *  根据sys 获取模版信息
     * @param sys source
     * @return list
     */
    List<WeChatTemplateMsg> getTemplateBySys(String sys);


    /**
     * 获取所有来源
     * @return sys
     */
    List<String> getAllSys();


    /**
     *  根据模版名称 获取模版信息
     * @param name name
     * @return temp
     */
    WeChatTemplateMsg getTemplateByName(String name);

}
