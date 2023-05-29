package com.gzw.kd.mapper;
import com.gzw.kd.common.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author gaozhiwei
 */
@Repository
@Mapper
public interface OrderMapper {

    /**
     * 根据id 获取用户订单信息
     * @param id id
     * @return order
     * @throws Exception err
     */

    OrderInfo getOrderById(String  id) throws  Exception;

    /**
     * 订单信息入库
     * @param orderInfo order
     * @return 1
     * @throws Exception err
     */

    Integer registerOrder(OrderInfo orderInfo)throws  Exception;



}
