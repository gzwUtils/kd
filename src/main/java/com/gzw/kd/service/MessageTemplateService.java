package com.gzw.kd.service;

import com.gzw.kd.common.entity.TemplateInfo;
import java.util.List;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 23:09
 */
public interface MessageTemplateService {

    /**
     * 查询模版列表
     * @param deleted  0：未删除 1：删除
     * @return list
     */
    List<TemplateInfo> findAllByIsDeleted(Integer deleted);


    /**
     * 统计未停用的条数
     *
     * @param deleted isDeleted o 启用 1 停用
     * @return long
     */
    Long countByIsDeletedEquals(Integer deleted);


    /**
     *  通过id 查询模版
     * @param id id
     * @return template
     */
    TemplateInfo selectById(Integer id);


    /**
     * 新增模版
     * @param templateInfo info
     * @return int
     */
    Integer registerTemplate(TemplateInfo templateInfo);
}
