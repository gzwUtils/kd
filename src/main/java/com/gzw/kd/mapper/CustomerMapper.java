package com.gzw.kd.mapper;


import com.gzw.kd.common.entity.Assign;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gaozhiwei
 */
@Repository
@Mapper
public interface CustomerMapper {

    /**
     * 根据openId 获取用户信息
     * @param openId openId
     * @return
     * @throws Exception
     */

    Assign getUserByOpenId(String  openId)throws  Exception;

    /**
     * 用户信息入库
     * @param user wx_user
     * @return
     * @throws Exception
     */

    Integer registerUser(Assign user)throws  Exception;

    /**
     * 更新用户信息
     * @param user wx
     * @return
     * @throws Exception
     */

    Integer updateAssignByOpenId(Assign user)throws  Exception;


    /**
     * 更新手机号
     * @param openId openId
     * @param  phone 手机号
     * @param name 真实姓名
     * @return
     * @throws Exception
     */

    Integer updatePhone(String openId,String phone,String name)throws  Exception;


    /**
     * 获取关注公众号成员列表
     * @return
     */
    List<String> getAllNames();

    /**
     * 根据name 获取用户信息
     * @param name name
     * @return
     * @throws Exception
     */

    Assign getUserByName(String  name)throws  Exception;



}
