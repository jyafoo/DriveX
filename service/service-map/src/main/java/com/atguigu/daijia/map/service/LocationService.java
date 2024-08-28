package com.atguigu.daijia.map.service;

import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;

import java.math.BigDecimal;
import java.util.List;

public interface LocationService {

    /**
     * 开启接单服务：更新司机经纬度位置
     *
     * @param updateDriverLocationForm 司机新位置信息的表单，用于更新数据库中的司机位置数据
     * @return 更新是否成功
     */
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    /**
     * 关闭接单服务：删除司机经纬度位置
     *
     * @param driverId 司机的唯一标识ID，用于指定要删除位置信息的司机
     * @return 布尔值，指示是否成功删除了司机的位置信息
     */
    Boolean removeDriverLocation(Long driverId);

    /**
     * 搜索附近满足条件的司机
     *
     * @param searchNearByDriverForm 附近司机搜索表单
     * @return 返回满足条件的附近司机列表封装在Result对象中
     */
    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    /**
     * 时更新司机赶往代驾起始点订单地址到缓存
     *
     * @param updateOrderLocationForm 订单位置更新表单，包含更新订单位置所需的信息
     * @return 布尔值，指示订单位置是否成功更新到缓存
     */
    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    /**
     * 获取代驾订单的经纬度位置
     *
     * @param orderId 订单ID，用于标识特定的订单
     * @return 返回一个Result对象，其中包含订单的位置信息（经度和纬度）
     */
    OrderLocationVo getCacheOrderLocation(Long orderId);

    /**
     * 批量保存代驾服务订单位置
     *
     * @param orderLocationServiceFormList 代驾服务订单位置信息列表，用于保存多个订单的位置信息
     * @return 布尔值，表示位置信息是否保存成功
     */
    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList);

    /**
     * 获取订单服务最后一个位置信息
     * 该方法通过订单ID查询并返回订单服务的最后一个位置信息，主要用于需要实时获取代驾服务位置的场景
     *
     * @param orderId 订单ID，用于标识特定的代驾服务订单
     * @return 订单服务的最后一个位置信息
     */
    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    /**
     * 计算指定订单的实际行驶里程。
     *
     * @param orderId 订单ID，用于标识特定的订单。
     * @return 订单的实际行驶里程信息。
     */
    BigDecimal calculateOrderRealDistance(Long orderId);
}
