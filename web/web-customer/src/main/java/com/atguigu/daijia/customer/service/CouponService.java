package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;

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


    PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit);
}
