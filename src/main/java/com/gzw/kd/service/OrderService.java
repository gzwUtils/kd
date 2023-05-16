package com.gzw.kd.service;
import com.gzw.kd.common.entity.OrderInfo;

/**
 * @author gaozhiwei
 */

@SuppressWarnings("all")
public interface OrderService {

    /**
     * 根据id 获取用户订单信息
     * @param id
     * @return
     * @throws Exception
     */

    OrderInfo getOrderById(String  id) throws  Exception;

    /**
     * 订单信息入库
     * @param orderInfo
     * @return
     * @throws Exception
     */

    Integer registerOrder(OrderInfo orderInfo)throws  Exception;

}
