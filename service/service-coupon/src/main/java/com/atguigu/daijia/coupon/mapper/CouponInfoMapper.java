package com.atguigu.daijia.coupon.mapper;

import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {


    /**
     * 查询未领取优惠券的分页信息
     *
     * @param pageParam 分页参数，用于控制返回结果的页码和每页数量
     * @param customerId 客户ID，用于筛选特定客户的未领取优惠券
     * @return 返回一个分页对象，包含客户未领取的优惠券信息
     */
    IPage<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId);

    /**
     * 查询未使用的优惠券列表
     *
     * @param pageParam 分页参数，用于控制分页查询的页码和每页数量
     * @param customerId 客户ID，用于筛选属于特定客户的优惠券
     * @return 返回分页查询结果PageVo<NoUseCouponVo>，包含未使用的优惠券信息和分页相关数据
     */
    IPage<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId);
}
