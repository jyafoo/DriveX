package com.atguigu.daijia.order.mapper;

import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.vo.order.OrderListVo;
import com.atguigu.daijia.model.vo.order.OrderPayVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    /**
     * 查询指定客户的订单分页列表
     *
     * @param pageParam 分页参数，用于指定分页行为的页面大小和当前页码
     * @param customerId 客户的唯一标识ID
     * @return 返回一个分页对象，包含订单列表和分页信息此方法用于在客户相关操作中检索订单信息，通过客户ID进行过滤，同时支持分页显示
     */
    IPage<OrderListVo> selectCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId);

    /**
     * 查询司机的订单分页列表
     *
     * @param pageParam 分页参数，用于指定分页查询的页码和每页显示数量
     * @param driverId 司机的ID，用于查询该司机相关的订单
     * @return 返回一个分页对象，包含订单列表和分页信息此方法旨在通过司机ID提供分页查询司机的订单信息功能
     */
    IPage<OrderListVo> selectDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);

    /**
     * 根据订单号和用户ID查询订单支付信息
     *
     * @param orderNo 订单号，用于唯一标识一个订单
     * @param customerId 用户ID，用于标识下单的用户
     * @return 返回订单的支付信息，包括但不限于支付状态、支付金额等
     */
    OrderPayVo selectOrderPayVo(@Param("orderNo")String orderNo, @Param("customerId")Long customerId);
}
