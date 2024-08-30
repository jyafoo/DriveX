package com.atguigu.daijia.driver.mapper;

import com.atguigu.daijia.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface DriverAccountMapper extends BaseMapper<DriverAccount> {

    /**
     * 添加奖励到司机账户表
     *
     * @param driverId
     * @param amount
     */
    void add(@Param("driverId") Long driverId, @Param("amount") BigDecimal amount);
}
