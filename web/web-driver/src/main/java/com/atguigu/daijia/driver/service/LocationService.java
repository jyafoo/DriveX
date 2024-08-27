package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;

import java.util.List;

public interface LocationService {


    /**
     * 开启接单服务：更新司机经纬度位置
     * @param updateDriverLocationForm
     * @return
     */
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    /**
     * 时更新司机赶往代驾起始点订单地址到缓存
     *
     * @param updateOrderLocationForm 订单位置更新表单，包含更新订单位置所需的信息
     * @return 布尔值，指示订单位置是否成功更新到缓存
     */
    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    /**
     * 批量保存代驾服务订单位置
     *
     * @param orderLocationServiceFormList 代驾服务订单位置信息列表，用于保存多个订单的位置信息
     * @return 布尔值，表示位置信息是否保存成功
     */
    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList);
}
