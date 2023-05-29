package com.gzw.kd.service.impl;

import com.gzw.kd.common.entity.OrderInfo;
import com.gzw.kd.mapper.OrderMapper;
import com.gzw.kd.service.OrderService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/15 17:53
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public OrderInfo getOrderById(String id) throws Exception {
        return orderMapper.getOrderById(id);
    }

    @Override
    public Integer registerOrder(OrderInfo orderInfo) throws Exception {
        return orderMapper.registerOrder(orderInfo);
    }
}
