package com.atguigu.daijia.order.service;

import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderBillForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.order.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 保存订单信息
     *
     * @param orderInfoForm 订单信息表单对象，包含要保存的订单信息
     * @return 返回保存结果，包括状态码和消息，以及保存的订单ID
     */
    Long saveOrderInfo(OrderInfoForm orderInfoForm);

    /**
     * 根据订单id获取订单状态
     *
     * @param orderId 订单的唯一标识符，用于查询订单状态
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 司机抢单接口
     *
     * @param driverId 抢单司机的唯一标识ID
     * @param orderId 待抢订单的唯一标识ID
     * @return 抢单操作是否成功的标识
     */
    Boolean robNewOrder(Long driverId, Long orderId);

    /**
     * 根据乘客ID查询当前正在进行的订单信息
     *
     * @param customerId 乘客的唯一标识ID
     * @return 返回当前订单的信息对象封装在Result中
     */
    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    /**
     * 根据司机ID查询当前正在进行的订单详情
     *
     * @param driverId 司机的唯一标识ID
     * @return 返回当前订单的信息封装在Result对象中
     */
    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    /**
     * 司机到达起始点接口
     * 该接口用于记录司机到达订单起始点的状态
     *
     * @param orderId 订单ID
     * @param driverId 司机ID
     * @return 返回一个结果对象，包含一个布尔值，指示司机是否成功到达起始点
     */
    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    /**
     * 更新代驾车辆信息
     *
     * @param updateOrderCartForm 包含更新车辆信息的表单
     * @return 返回一个结果对象，对象中包含一个布尔值，表示更新是否成功
     */
    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);

    /**
     * 开始代驾服务
     *
     * @param startDriveForm 包含开始代驾所需信息的表单对象
     * @return 返回一个Result对象，其中包含一个Boolean值，表示代驾服务是否成功开始
     */
    Boolean startDrive(StartDriveForm startDriveForm);

    /**
     * 根据给定的时间段，查询并返回该时间段内的订单数量。
     *
     * @param startTime 查询时间段的开始时间，格式为字符串，确保查询的起始点准确。
     * @param endTime 查询时间段的结束时间，格式为字符串，确保查询的截止点准确。
     * @return 订单总数。
     */
    Long getOrderNumByTime(String startTime, String endTime);

    /**
     * 结束代驾服务并更新订单账单
     *
     * @param updateOrderBillForm 包含更新订单账单所需信息的请求体
     * @return 订单账单是否更新成功
     */
    Boolean endDrive(UpdateOrderBillForm updateOrderBillForm);

    /**
     * 获取乘客端订单分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    PageVo findCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId);

    /**
     * 获取司机端订单分页列表
     */
    PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);

    /**
     * 根据订单id获取实际账单信息
     *
     * @param orderId 订单ID
     * @return 返回订单账单信息的查询结果
     */
    OrderBillVo getOrderBillInfo(Long orderId);

    /**
     * 根据订单id获取实际分账信息
     *
     * @param orderId 订单ID
     * @return 分账信息封装在Result对象中
     */
    OrderProfitsharingVo getOrderProfitsharing(Long orderId);

    /**
     * 发送账单信息
     *
     * @param orderId 订单ID，用于标识特定的订单
     * @param driverId 司机ID，用于标识账单信息发送的目标司机
     * @return 布尔值，表示发送是否成功
     */
    Boolean sendOrderBillInfo(Long orderId, Long driverId);

    /**
     * 获取订单支付信息
     *
     * @param orderNo   订单号，唯一标识一个订单
     * @param customerId 客户ID，用于确认请求的客户身份
     * @return 订单支付信息的OrderPayVo对象如果获取失败，可能返回错误信息
     */
    OrderPayVo getOrderPayVo(String orderNo, Long customerId);

    /**
     * 更改订单支付状态
     *
     * @param orderNo 订单编号，用于识别特定订单
     * @return 操作结果，指示支付状态是否更新成功
     */
    Boolean updateOrderPayStatus(String orderNo);

    /**
     * 获取订单的系统奖励信息
     *
     * @param orderNo 订单编号，用于识别特定的订单
     * @return 返回一个封装了订单奖励信息的结果对象
     */
    OrderRewardVo getOrderRewardFee(String orderNo);

    /**
     * 取消指定的订单
     *
     * @param orderId 订单ID，用于标识需要取消的订单
     */
    void orderCancel(long orderId);

    /**
     * 更新订单优惠券金额
     *
     * @param orderId 订单ID
     * @param couponAmount 优惠券金额
     * @return 操作结果，包含是否成功的信息
     */
    Boolean updateCouponAmount(Long orderId, BigDecimal couponAmount);
}
