package com.atguigu.daijia.order.mapper;

import com.atguigu.daijia.model.entity.order.OrderBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface OrderBillMapper extends BaseMapper<OrderBill> {


    /**
     * 更新订单的优惠券金额
     *
     * @param orderId 订单ID，用于定位特定订单
     * @param couponAmount 新的优惠券金额，用于更新订单的优惠信息
     * @return 更新操作影响的行数，以验证操作是否成功
     */
    int updateCouponAmount(@Param("orderId") Long orderId, @Param("couponAmount") BigDecimal couponAmount);
}
