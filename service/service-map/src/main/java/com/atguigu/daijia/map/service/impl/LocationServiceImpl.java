package com.atguigu.daijia.map.service.impl;


import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final RedisTemplate redisTemplate;
    private final DriverInfoFeignClient driverInfoFeignClient;


    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        /**
         *  Redis GEO 主要用于存储地理位置信息，并对存储的信息进行相关操作，该功能在 Redis 3.2 版本新增。
         *  后续用在，乘客下单后寻找5公里范围内开启接单服务的司机，通过Redis GEO进行计算
         */
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION, point, updateDriverLocationForm.getDriverId().toString());
        return true;
    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());
        return true;
    }

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {
        // 搜索经纬度位置5公里以内的司机
        // 定义经纬度点
        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(), searchNearByDriverForm.getLatitude().doubleValue());

        // 定义距离：5km（系统配置）
        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS);
        // 定义以point为中心，distance为距离的范围
        Circle circle = new Circle(point, distance);

        // 定义GEO参数,设置返回结果包含的内容
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance() // 包含距离
                .includeCoordinates() // 包含坐标
                .sortAscending(); // 排序：升序

        // 1、GEORADIUS获取附近范围内的信息
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = redisTemplate.opsForGeo().radius(
                RedisConstant.DRIVER_GEO_LOCATION,
                circle,
                args
        );

        // 2、收集信息，存入list
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();

        // 3、返回计算后的信息
        ArrayList<NearByDriverVo> list = new ArrayList<>();

        if(!CollectionUtils.isEmpty(content)){
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();
            while(iterator.hasNext()){
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

                long driverId = Long.parseLong(item.getContent().getName());
                BigDecimal curDistance = new BigDecimal(item
                        .getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                log.info("司机：{}，距离：{}",driverId,item.getDistance().getValue());

                //获取司机接单设置参数
                DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();

                // 接单里程判断，acceptDistance==0：不限制
                if(driverSet.getAcceptDistance().doubleValue() != 0 &&
                        driverSet.getAcceptDistance().subtract(curDistance).doubleValue() < 0){
                    continue;
                }

                // 订单里程判断，orderDistance==0：不限制
                if(driverSet.getOrderDistance().doubleValue() != 0 &&
                driverSet.getOrderDistance().subtract(searchNearByDriverForm.getMileageDistance()).doubleValue() < 0){
                    continue;
                }
                // 满足条件的附近司机信息
                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(curDistance);
                list.add(nearByDriverVo);
            }
        }
        return list;
    }


}
