package com.gzw.kd.mapper;



import com.gzw.kd.common.entity.WeChatTemplateMsg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author 高志伟
 */
@Repository
@Mapper
public interface TemplateMapper {

    /**
     * 根据role 获取模板信息
     * @param role role
     * @return list
     * @throws Exception err
     */

    List<WeChatTemplateMsg> getTemplateByRole(Integer role )throws  Exception;



}
