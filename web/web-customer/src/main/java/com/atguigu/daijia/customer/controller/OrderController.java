package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
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
    @Operation(summary = "查找乘客端当前订单")
    @LoginCheck
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        // TODO (JIA,2024/8/15,12:41) 后续完善，先假设乘客没有正在进行中的订单
        currentOrderInfoVo.setIsHasCurrentOrder(false);
        return Result.ok(currentOrderInfoVo);
    }

    // TODO (JIA,2024/8/18,11:47) 亮点二：通过Drools规则引擎预估乘客的订单数据（价格）
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

}

