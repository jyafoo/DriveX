package com.atguigu.daijia.order.controller;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderBillForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.order.*;
import com.atguigu.daijia.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@Tag(name = "订单API接口管理")
@RestController
@RequestMapping(value="/order/info")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class OrderInfoController {
    private final OrderInfoService orderInfoService;

    /**
     * 保存订单信息
     *
     * @param orderInfoForm 订单信息表单对象，包含要保存的订单信息
     * @return 返回保存结果，包括状态码和消息，以及保存的订单ID
     */
    @Operation(summary = "保存订单信息")
    @PostMapping("/saveOrderInfo")
    public Result<Long> saveOrderInfo(@RequestBody OrderInfoForm orderInfoForm) {
        return Result.ok(orderInfoService.saveOrderInfo(orderInfoForm));
    }

    /**
     * 根据订单id获取订单状态
     *
     * @param orderId 订单的唯一标识符，用于查询订单状态
     * @return 订单状态
     */
    @Operation(summary = "根据订单id获取订单状态")
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.getOrderStatus(orderId));
    }

    /**
     * 司机抢单接口
     *
     * @param driverId 抢单司机的唯一标识ID
     * @param orderId 待抢订单的唯一标识ID
     * @return 抢单操作是否成功的标识
     */
    @Operation(summary = "司机抢单")
    @GetMapping("/robNewOrder/{driverId}/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable Long driverId, @PathVariable Long orderId) {
        return Result.ok(orderInfoService.robNewOrder(driverId, orderId));
    }

    /**
     * 根据乘客ID查询当前正在进行的订单信息
     *
     * @param customerId 乘客的唯一标识ID
     * @return 返回当前订单的信息对象封装在Result中
     */
    @Operation(summary = "乘客端查找当前订单")
    @GetMapping("/searchCustomerCurrentOrder/{customerId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(@PathVariable Long customerId) {
        return Result.ok(orderInfoService.searchCustomerCurrentOrder(customerId));
    }

    /**
     * 根据司机ID查询当前正在进行的订单详情
     *
     * @param driverId 司机的唯一标识ID
     * @return 返回当前订单的信息封装在Result对象中
     */
    @Operation(summary = "司机端查找当前订单")
    @GetMapping("/searchDriverCurrentOrder/{driverId}")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder(@PathVariable Long driverId) {
        return Result.ok(orderInfoService.searchDriverCurrentOrder(driverId));
    }

    /**
     * 根据订单id获取订单信息
     *
     * @param orderId 订单id
     * @return 返回订单信息的查询结果
     */
    @Operation(summary = "根据订单id获取订单信息")
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.getById(orderId));
    }

    /**
     * 司机到达起始点接口
     * 该接口用于记录司机到达订单起始点的状态
     *
     * @param orderId 订单ID
     * @param driverId 司机ID
     * @return 返回一个结果对象，包含一个布尔值，指示司机是否成功到达起始点
     */
    @Operation(summary = "司机到达起始点")
    @GetMapping("/driverArriveStartLocation/{orderId}/{driverId}")
    public Result<Boolean> driverArriveStartLocation(@PathVariable Long orderId, @PathVariable Long driverId) {
        return Result.ok(orderInfoService.driverArriveStartLocation(orderId, driverId));
    }

    /**
     * 更新代驾车辆信息
     *
     * @param updateOrderCartForm 包含更新车辆信息的表单
     * @return 返回一个结果对象，对象中包含一个布尔值，表示更新是否成功
     */
    @Operation(summary = "更新代驾车辆信息")
    @PostMapping("/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm) {
        return Result.ok(orderInfoService.updateOrderCart(updateOrderCartForm));
    }

    /**
     * 开始代驾服务
     *
     * @param startDriveForm 包含开始代驾所需信息的表单对象
     * @return 返回一个Result对象，其中包含一个Boolean值，表示代驾服务是否成功开始
     */
    @Operation(summary = "开始代驾服务")
    @PostMapping("/startDrive")
    public Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm) {
        return Result.ok(orderInfoService.startDrive(startDriveForm));
    }

    /**
     * 根据给定的时间段，查询并返回该时间段内的订单数量。
     *
     * @param startTime 查询时间段的开始时间，格式为字符串，确保查询的起始点准确。
     * @param endTime 查询时间段的结束时间，格式为字符串，确保查询的截止点准确。
     * @return 订单总数。
     */
    @Operation(summary = "根据时间段获取订单数")
    @GetMapping("/getOrderNumByTime/{startTime}/{endTime}")
    public Result<Long> getOrderNumByTime(@PathVariable String startTime, @PathVariable String endTime) {
        return Result.ok(orderInfoService.getOrderNumByTime(startTime, endTime));
    }

    /**
     * 结束代驾服务并更新订单账单
     *
     * @param updateOrderBillForm 包含更新订单账单所需信息的请求体
     * @return 订单账单是否更新成功
     */
    @Operation(summary = "结束代驾服务更新订单账单")
    @PostMapping("/endDrive")
    public Result<Boolean> endDrive(@RequestBody UpdateOrderBillForm updateOrderBillForm) {
        return Result.ok(orderInfoService.endDrive(updateOrderBillForm));
    }

    /**
     * 获取乘客端订单分页列表
     */
    @Operation(summary = "获取乘客订单分页列表")
    @GetMapping("/findCustomerOrderPage/{customerId}/{page}/{limit}")
    public Result<PageVo> findCustomerOrderPage(
            @Parameter(name = "customerId", description = "乘客id", required = true)
            @PathVariable Long customerId,

            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        PageVo pageVo = orderInfoService.findCustomerOrderPage(pageParam, customerId);
        pageVo.setPage(page);
        pageVo.setLimit(limit);
        return Result.ok(pageVo);
    }

    /**
     * 获取司机端订单分页列表
     */
    @Operation(summary = "获取司机订单分页列表")
    @GetMapping("/findDriverOrderPage/{driverId}/{page}/{limit}")
    public Result<PageVo> findDriverOrderPage(
            @Parameter(name = "driverId", description = "司机id", required = true)
            @PathVariable Long driverId,

            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        PageVo pageVo = orderInfoService.findDriverOrderPage(pageParam, driverId);
        pageVo.setPage(page);
        pageVo.setLimit(limit);
        return Result.ok(pageVo);
    }

    /**
     * 根据订单id获取实际账单信息
     *
     * @param orderId 订单ID
     * @return 返回订单账单信息的查询结果
     */
    @Operation(summary = "根据订单id获取实际账单信息")
    @GetMapping("/getOrderBillInfo/{orderId}")
    public Result<OrderBillVo> getOrderBillInfo(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.getOrderBillInfo(orderId));
    }

    /**
     * 根据订单id获取实际分账信息
     *
     * @param orderId 订单ID
     * @return 分账信息封装在Result对象中
     */
    @Operation(summary = "根据订单id获取实际分账信息")
    @GetMapping("/getOrderProfitsharing/{orderId}")
    public Result<OrderProfitsharingVo> getOrderProfitsharing(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.getOrderProfitsharing(orderId));
    }

    /**
     * 发送账单信息
     *
     * @param orderId 订单ID，用于标识特定的订单
     * @param driverId 司机ID，用于标识账单信息发送的目标司机
     * @return 布尔值，表示发送是否成功
     */
    @Operation(summary = "发送账单信息")
    @GetMapping("/sendOrderBillInfo/{orderId}/{driverId}")
    Result<Boolean> sendOrderBillInfo(@PathVariable Long orderId, @PathVariable Long driverId) {
        return Result.ok(orderInfoService.sendOrderBillInfo(orderId, driverId));
    }

    /**
     * 获取订单支付信息
     *
     * @param orderNo   订单号，唯一标识一个订单
     * @param customerId 客户ID，用于确认请求的客户身份
     * @return 订单支付信息的OrderPayVo对象如果获取失败，可能返回错误信息
     */
    @Operation(summary = "获取订单支付信息")
    @GetMapping("/getOrderPayVo/{orderNo}/{customerId}")
    public Result<OrderPayVo> getOrderPayVo(@PathVariable String orderNo, @PathVariable Long customerId) {
        return Result.ok(orderInfoService.getOrderPayVo(orderNo, customerId));
    }

    /**
     * 更改订单支付状态
     *
     * @param orderNo 订单编号，用于识别特定订单
     * @return 操作结果，指示支付状态是否更新成功
     */
    @Operation(summary = "更改订单支付状态")
    @GetMapping("/updateOrderPayStatus/{orderNo}")
    public Result<Boolean> updateOrderPayStatus(@PathVariable String orderNo) {
        return Result.ok(orderInfoService.updateOrderPayStatus(orderNo));
    }

    /**
     * 获取订单的系统奖励信息
     *
     * @param orderNo 订单编号，用于识别特定的订单
     * @return 返回一个封装了订单奖励信息的结果对象
     */
    @Operation(summary = "获取订单的系统奖励")
    @GetMapping("/getOrderRewardFee/{orderNo}")
    public Result<OrderRewardVo> getOrderRewardFee(@PathVariable String orderNo) {
        return Result.ok(orderInfoService.getOrderRewardFee(orderNo));
    }

    @Operation(summary = "更新订单优惠券金额")
    @GetMapping("/updateCouponAmount/{orderId}/{couponAmount}")
    public Result<Boolean> updateCouponAmount(@PathVariable Long orderId, @PathVariable BigDecimal couponAmount) {
        return Result.ok(orderInfoService.updateCouponAmount(orderId, couponAmount));
    }
}

