package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;

import java.util.List;

public interface OrderService {


    /**
     * 查询订单状态
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 根据司机ID查询新的订单队列数据
     *
     * @param driverId 司机的ID
     * @return 新订单数据的列表，订单数据与指定的司机相关
     */
    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    /**
     * 司机抢单接口
     * 此接口允许已登录的司机用户尝试抢夺指定的订单
     * 抢单成功意味着订单状态将被更新，以表示该司机将执行此订单
     *
     * @param orderId 订单ID，用于标识司机想要抢夺的订单
     * @return 抢单操作的结果，包含一个布尔值，true表示抢单成功，false表示抢单失败
     */
    Boolean robNewOrder(Long driverId, Long orderId);

    /**
     * 根据司机ID查询当前正在进行的订单详情
     *
     * @param driverId 司机的唯一标识ID
     * @return 返回当前订单的信息封装在Result对象中
     */
    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    /**
     * 司机端获取订单信息
     * 此方法主要用于获取特定司机承接的订单详细信息，旨在支持订单跟踪和管理功能
     *
     * @param orderId 订单的唯一标识，用于定位特定的订单记录
     * @param driverId 司机的唯一标识，用于关联订单与服务司机
     * @return 返回一个OrderInfoVo对象，包含了订单的详细信息，包括但不限于订单状态、乘客信息、行程详情等
     */
    OrderInfoVo getOrderInfo(Long orderId, Long driverId);

    /**
     * 计算最佳驾驶线路
     *
     * @param calculateDrivingLineForm 驾驶线路计算表单数据，包含计算最佳线路所需的所有参数
     * @return 返回一个封装了最佳驾驶线路信息的Result对象
     */
    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
}
