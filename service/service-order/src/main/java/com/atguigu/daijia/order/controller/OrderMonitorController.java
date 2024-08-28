package com.atguigu.daijia.order.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.entity.order.OrderMonitor;
import com.atguigu.daijia.model.entity.order.OrderMonitorRecord;
import com.atguigu.daijia.order.service.OrderMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order/monitor")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class OrderMonitorController {

    private final OrderMonitorService orderMonitorService;

    /**
     * 保存订单监控记录数据
     *
     * @param orderMonitorRecord 订单监控记录对象，包含要保存的订单监控数据
     * @return 布尔值，表示保存操作是否成功
     */
    @Operation(summary = "保存订单监控记录数据")
    @PostMapping("/saveOrderMonitorRecord")
    public Result<Boolean> saveMonitorRecord(@RequestBody OrderMonitorRecord orderMonitorRecord) {
        return Result.ok(orderMonitorService.saveOrderMonitorRecord(orderMonitorRecord));
    }

    /**
     * 根据订单id获取订单监控信息
     *
     * @param orderId 订单ID
     * @return 订单监控信息
     */
    @Operation(summary = "根据订单id获取订单监控信息")
    @GetMapping("/getOrderMonitor/{orderId}")
    public Result<OrderMonitor> getOrderMonitor(@PathVariable Long orderId) {
        return Result.ok(orderMonitorService.getOrderMonitor(orderId));
    }

    /**
     * 更新订单监控信息
     *
     * @param orderMonitor 订单监控对象，包含要更新的订单监控信息
     * @return 布尔值，表示更新操作是否成功
     */
    @Operation(summary = "更新订单监控信息")
    @PostMapping("/updateOrderMonitor")
    public Result<Boolean> updateOrderMonitor(@RequestBody OrderMonitor orderMonitor) {
        return Result.ok(orderMonitorService.updateOrderMonitor(orderMonitor));
    }

}

