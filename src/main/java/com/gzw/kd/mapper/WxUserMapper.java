package com.gzw.kd.mapper;


import com.gzw.kd.common.entity.WxUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gaozhiwei
 */
@Repository
@Mapper
public interface WxUserMapper {

    /**
     * 根据openId 获取用户信息
     * @param openId openId
     * @return
     * @throws Exception
     */

    WxUserInfo getUserByOpenId(String  openId)throws  Exception;

    /**
     * 用户信息入库
     * @param user wx_user
     * @return
     * @throws Exception
     */

    Integer registerUser(WxUserInfo user)throws  Exception;

    /**
     * 更新用户信息
     * @param user wx
     * @return
     * @throws Exception
     */

    Integer updatePhoneByOpenId(WxUserInfo user)throws  Exception;


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

    WxUserInfo getUserByName(String  name)throws  Exception;



}
