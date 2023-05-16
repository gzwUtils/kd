package com.gzw.kd.service;


import com.gzw.kd.common.entity.Assign;
import java.util.List;

/**
 * @author gaozhiwei
 */

@SuppressWarnings("all")
public interface CustomerService {

    /**
     * 根据openId 获取用户合同信息
     * @param openId
     * @return
     * @throws Exception
     */

    Assign getUserByOpenId(String  openId) throws  Exception;

    /**
     * 用户合同信息入库
     * @param user
     * @return
     * @throws Exception
     */

    Integer registerUser(Assign user)throws  Exception;

    /**
     * 更新服务信息
     * @param user
     * @return
     * @throws Exception
     */

    Integer updateAssignByOpenId(Assign user)throws  Exception;

    /**
     * 更新手机号
     * @param openId openId
     * @param  phone 手机号
     * @param  name 真实姓名
     * @return
     * @throws Exception
     */

    Integer updatePhone(String openId,String phone,String name)throws  Exception;


    /**
     * 获取关注公众号成员列表
     * @return
     */
    List<String>  getAllNames();


    /**
     * 根据name 获取用户信息
     * @param name
     * @return
     * @throws Exception
     */

    Assign getUserByName(String  name)throws  Exception;

}
