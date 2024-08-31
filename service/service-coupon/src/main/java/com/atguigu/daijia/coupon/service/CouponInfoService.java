package com.atguigu.daijia.coupon.service;

import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CouponInfoService extends IService<CouponInfo> {


    /**
     * 查询未领取优惠券分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId);

    /**
     * 查询未使用的优惠券列表
     *
     * @param pageParam 分页参数，用于控制分页查询的页码和每页数量
     * @param customerId 客户ID，用于筛选属于特定客户的优惠券
     * @return 返回分页查询结果PageVo<NoUseCouponVo>，包含未使用的优惠券信息和分页相关数据
     */
    PageVo<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId);

    /**
     * 查询已使用的优惠券分页数据
     *
     * @param pageParam 分页参数，用于控制分页行为
     * @param customerId 客户ID，用于筛选特定客户的已使用优惠券
     * @return 返回一个PageVo对象，包含分页后的已使用优惠券信息
     */
    PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId);
}
