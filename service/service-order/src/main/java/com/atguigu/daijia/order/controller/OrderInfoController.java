package com.atguigu.daijia.order.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
}

