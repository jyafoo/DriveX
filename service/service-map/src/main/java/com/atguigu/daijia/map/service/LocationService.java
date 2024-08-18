package com.atguigu.daijia.map.service;

import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;

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
}
