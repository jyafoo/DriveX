package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;

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
}
