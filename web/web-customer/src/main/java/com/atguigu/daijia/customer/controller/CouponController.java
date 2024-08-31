package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CouponService;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "优惠券活动接口管理")
@RestController
@RequestMapping(value="/coupon")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


    /**
     * 查询未领取优惠券的分页信息
     *
     * @param page 当前页码，用于分页查询，标识查询结果的哪一页
     * @param limit 每页记录数，用于分页查询，标识每页应显示的记录数
     * @return 返回一个PageVo对象，包含未领取优惠券的分页信息
     */
    @Operation(summary = "查询未领取优惠券分页列表")
    @LoginCheck
    @GetMapping("findNoReceivePage/{page}/{limit}")
    public Result<PageVo<NoReceiveCouponVo>> findNoReceivePage(
            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit) {
        Long customerId = AuthContextHolder.getUserId();
        PageVo<NoReceiveCouponVo> pageVo = couponService.findNoReceivePage(customerId, page, limit);
        return Result.ok(pageVo);
    }

    /**
     * 查询未使用的优惠券分页列表
     *
     * @param page 当前页码，用于分页查询
     * @param limit 每页显示的数量，用于分页查询
     * @return 返回一个PageVo对象，包含未使用的NoUseCouponVo类型的分页数据
     */
    @Operation(summary = "查询未使用优惠券分页列表")
    @LoginCheck
    @GetMapping("findNoUsePage/{page}/{limit}")
    public Result<PageVo<NoUseCouponVo>> findNoUsePage(
            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit) {
        Long customerId = AuthContextHolder.getUserId();
        PageVo<NoUseCouponVo> pageVo = couponService.findNoUsePage(customerId, page, limit);
        return Result.ok(pageVo);
    }

    /**
     * 查询已使用的优惠券分页数据
     *
     * @return 返回一个PageVo对象，包含分页后的已使用优惠券信息
     */
    @Operation(summary = "查询已使用优惠券分页列表")
    @LoginCheck
    @GetMapping("findUsedPage/{page}/{limit}")
    public Result<PageVo<UsedCouponVo>> findUsedPage(
            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit) {
        Long customerId = AuthContextHolder.getUserId();
        PageVo<UsedCouponVo> pageVo = couponService.findUsedPage(customerId, page, limit);
        return Result.ok(pageVo);
    }

    /**
     * 用户领取优惠券的接口
     *
     * @param couponId 领取的优惠券ID，用于标识特定的优惠券
     * @return 返回一个Result对象，其中包含一个Boolean值，表示领取优惠券的操作是否成功
     */
    @Operation(summary = "领取优惠券")
    @LoginCheck
    @GetMapping("/receive/{couponId}")
    public Result<Boolean> receive(@PathVariable Long couponId) {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(couponService.receive(customerId, couponId));
    }

    /**
     * 获取未使用的最佳优惠券信息
     *
     * @return 返回一个Result对象，其中包含客户未使用的最佳优惠券信息
     */
    @Operation(summary = "获取未使用的最佳优惠券信息")
    @LoginCheck
    @GetMapping("/findAvailableCoupon/{orderId}")
    public Result<List<AvailableCouponVo>> findAvailableCoupon(@PathVariable Long orderId) {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(couponService.findAvailableCoupon(customerId, orderId));
    }



}

