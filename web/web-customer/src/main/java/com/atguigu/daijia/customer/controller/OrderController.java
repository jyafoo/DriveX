package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 预估订单数据
     *
     * @param expectOrderForm 用户下单的所有必要信息
     * @return ExpectOrderVo 用户期望生成的订单信息
     */
    @Operation(summary = "预估订单数据")
    @LoginCheck
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm) {
        return Result.ok(orderService.expectOrder(expectOrderForm));
    }

    /**
     * 提交订单
     * @param submitOrderForm 订单表单
     * @return 订单id
     */
    @Operation(summary = "乘客下单")
    @LoginCheck
    @PostMapping("/submitOrder")
    public Result<Long> submitOrder(@RequestBody SubmitOrderForm submitOrderForm) {
        submitOrderForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(orderService.submitOrder(submitOrderForm));
    }

    /**
     * 查询订单状态
     * 该方法需要用户登录权限，用于查询指定订单的状态
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return 订单状态
     */
    @Operation(summary = "查询订单状态")
    @LoginCheck
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderStatus(orderId));
    }

    /**
     * 根据乘客ID查询当前正在进行的订单信息
     *
     * @return 返回当前订单的信息对象封装在Result中
     */
    @Operation(summary = "乘客端查找当前订单")
    @LoginCheck
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchCustomerCurrentOrder(customerId));
    }

    /**
     * 乘客端获取订单信息
     * 该方法需要用户登录认证
     *
     * @param orderId 订单ID
     * @return 返回订单信息的封装对象
     */
    @Operation(summary = "乘客端获取订单信息")
    @LoginCheck
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable Long orderId) {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getOrderInfo(orderId, customerId));
    }

    /**
     * 根据订单id获取司机基本信息
     * 该方法需要用户登录后才能访问，返回指定订单的司机信息
     *
     * @param orderId 订单ID，用于识别特定的订单
     * @return 返回DriverInfoVo对象，包含司机的基本信息
     */
    @Operation(summary = "根据订单id获取司机基本信息")
    @LoginCheck
    @GetMapping("/getDriverInfo/{orderId}")
    public Result<DriverInfoVo> getDriverInfo(@PathVariable Long orderId) {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getDriverInfo(orderId, customerId));
    }

    /**
     * 获取代驾订单的经纬度位置
     *
     * @param orderId 订单ID，用于标识特定的订单
     * @return 返回一个Result对象，其中包含订单的位置信息（经度和纬度）
     */
    @Operation(summary = "司机赶往代驾起始点：获取订单经纬度位置")
    @LoginCheck
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getOrderLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getCacheOrderLocation(orderId));
    }

    /**
     * 获取订单服务最后一个位置信息
     * 该方法通过订单ID查询并返回订单服务的最后一个位置信息，主要用于需要实时获取代驾服务位置的场景
     *
     * @param orderId 订单ID，用于标识特定的代驾服务订单
     * @return 订单服务的最后一个位置信息
     */
    @Operation(summary = "代驾服务：获取订单服务最后一个位置信息")
    @LoginCheck
    @GetMapping("/getOrderServiceLastLocation/{orderId}")
    public Result<OrderServiceLastLocationVo> getOrderServiceLastLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderServiceLastLocation(orderId));
    }

}

