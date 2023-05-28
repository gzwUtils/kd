package com.gzw.kd.mapper;
import com.gzw.kd.common.entity.TemplateInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 22:59
 */
@Repository
@Mapper
public interface MessageTemplateMapper {

    /**
     *   查询所有模版
     * @param deleted  0：启用 1：停用
     * @return list
     */
    List<TemplateInfo> findAllByIsDeleted(Integer deleted);


    /**
     * 统计未删除的条数
     *
     * @param deleted 0：启用 1：停用
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
