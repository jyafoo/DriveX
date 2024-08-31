package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;

public interface CouponService  {


    /**
     * 查询未领取优惠券的分页信息
     *
     * @param customerId 客户ID，标识数据库中唯一的客户
     * @param page 当前页码，用于分页查询，标识查询结果的哪一页
     * @param limit 每页记录数，用于分页查询，标识每页应显示的记录数
     * @return 返回一个PageVo对象，包含未领取优惠券的分页信息
     */
    PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit);


    /**
     * 查询未使用的优惠券分页列表
     *
     * @param customerId 用户ID，用于定位特定用户的优惠券信息
     * @param page 当前页码，用于分页查询
     * @param limit 每页显示的数量，用于分页查询
     * @return 返回一个PageVo对象，包含未使用的NoUseCouponVo类型的分页数据
     */
    PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit);

    /**
     * 查询已使用的优惠券分页数据
     *
     * @param customerId 客户ID，用于筛选特定客户的已使用优惠券
     * @return 返回一个PageVo对象，包含分页后的已使用优惠券信息
     */
    PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit);

    /**
     * 用户领取优惠券的接口
     *
     * @param customerId 领取优惠券的用户ID，用于标识特定的用户
     * @param couponId 领取的优惠券ID，用于标识特定的优惠券
     * @return 返回一个Result对象，其中包含一个Boolean值，表示领取优惠券的操作是否成功
     */
    Boolean receive(Long customerId, Long couponId);
}
