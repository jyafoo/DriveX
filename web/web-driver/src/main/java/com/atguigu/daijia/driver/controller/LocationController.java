package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping(value="/location")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class LocationController {
    private LocationService locationService;

    /**
     * 开启接单服务：更新司机经纬度位置
     * @param updateDriverLocationForm
     * @return
     */
    @Operation(summary = "开启接单服务：更新司机经纬度位置")
    @LoginCheck
    @PostMapping("/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm updateDriverLocationForm) {
        Long driverId = AuthContextHolder.getUserId();
        updateDriverLocationForm.setDriverId(driverId);
        return Result.ok(locationService.updateDriverLocation(updateDriverLocationForm));
    }


    /**
     * 时更新司机赶往代驾起始点订单地址到缓存
     *
     * @param updateOrderLocationForm 订单位置更新表单，包含更新订单位置所需的信息
     * @return 布尔值，指示订单位置是否成功更新到缓存
     */
    @Operation(summary = "司机赶往代驾起始点：更新订单位置到Redis缓存")
    @LoginCheck
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm updateOrderLocationForm) {
        return Result.ok(locationService.updateOrderLocationToCache(updateOrderLocationForm));
    }

}

