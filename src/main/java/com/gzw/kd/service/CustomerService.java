package com.gzw.kd.service;


import com.gzw.kd.common.entity.Assign;

import java.math.BigDecimal;
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
     * 更新用户合同信息
     * @param user
     * @return
     * @throws Exception
     */

    Integer updatePhoneByOpenId(Assign user)throws  Exception;

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
     * 更新余额
     * @param openId openId
     * @param  balance 余额
     * @param  coupon 是否使用优惠劵
     *  @param  couponBalance 优惠劵额度
     * @return
     * @throws Exception
     */

    Integer updateBalance(String openId, BigDecimal balance,int coupon,BigDecimal couponBalance) throws  Exception;

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
