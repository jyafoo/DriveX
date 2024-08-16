package com.atguigu.daijia.map.service;

import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;

public interface MapService {

    /**
     * 计算驾驶线路接口
     * 用于接收前端提交的驾驶线路计算表单，通过调用地图服务计算出驾驶线路信息，并返回给前端
     *
     * @param calculateDrivingLineForm 驾驶线路计算表单，包含起点、终点的经纬度信息
     * @return 驾驶线路信息对象
     */
    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
}
