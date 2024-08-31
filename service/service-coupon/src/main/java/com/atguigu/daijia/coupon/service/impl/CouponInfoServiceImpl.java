package com.atguigu.daijia.coupon.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.coupon.mapper.CouponInfoMapper;
import com.atguigu.daijia.coupon.mapper.CustomerCouponMapper;
import com.atguigu.daijia.coupon.service.CouponInfoService;
import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.entity.coupon.CustomerCoupon;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    private final CouponInfoMapper couponInfoMapper;
    private final RedissonClient redissonClient;

    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<NoReceiveCouponVo> pageInfo = couponInfoMapper.findNoReceivePage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<NoUseCouponVo> pageInfo = couponInfoMapper.findNoUsePage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<UsedCouponVo> pageInfo = couponInfoMapper.findUsedPage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    private CustomerCouponMapper customerCouponMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean receive(Long customerId, Long couponId) {

        //1、查询优惠券
        CouponInfo couponInfo = this.getById(couponId);
        if(null == couponInfo) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //2、优惠券过期日期判断
        if (couponInfo.getExpireTime().before(new Date())) {
            throw new GuiguException(ResultCodeEnum.COUPON_EXPIRE);
        }

        //3、校验库存，优惠券领取数量判断
        if (couponInfo.getPublishCount() !=0 && couponInfo.getReceiveCount() >= couponInfo.getPublishCount()) {
            throw new GuiguException(ResultCodeEnum.COUPON_LESS);
        }

        /*//4、校验每人限领数量
        if (couponInfo.getPerLimit() > 0) {
            //4.1、统计当前用户对当前优惠券的已经领取的数量
            long count = customerCouponMapper.selectCount(new LambdaQueryWrapper<CustomerCoupon>().eq(CustomerCoupon::getCouponId, couponId).eq(CustomerCoupon::getCustomerId, customerId));
            //4.2、校验限领数量
            if (count >= couponInfo.getPerLimit()) {
                throw new GuiguException(ResultCodeEnum.COUPON_USER_LIMIT);
            }
        }

        //5、更新优惠券领取数量
        int row = couponInfoMapper.updateReceiveCount(couponId);
        if (row == 1) {
            //6、保存领取记录
            this.saveCustomerCoupon(customerId, couponId, couponInfo.getExpireTime());
            return true;
        }*/

        // TODO (JIA,2024/8/31,21:28) 亮点八：优惠券库存超卖问题的解决：通过乐观锁（版本号）判断优惠券领取数量，通过悲观锁（redissonLock）解决限领数量和发行数量
        RLock lock = null;
        try {
            // 初始化分布式锁
            //每人领取限制  与 优惠券发行总数 必须保证原子性，使用customerId减少锁的粒度，增加并发能力
            lock = redissonClient.getLock(RedisConstant.COUPON_LOCK + customerId);
            boolean flag = lock.tryLock(RedisConstant.COUPON_LOCK_WAIT_TIME, RedisConstant.COUPON_LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (flag) {
                //4、校验每人限领数量
                if (couponInfo.getPerLimit() > 0) {
                    //4.1、统计当前用户对当前优惠券的已经领取的数量
                    long count = customerCouponMapper.selectCount(new LambdaQueryWrapper<CustomerCoupon>().eq(CustomerCoupon::getCouponId, couponId).eq(CustomerCoupon::getCustomerId, customerId));
                    //4.2、校验限领数量
                    if (count >= couponInfo.getPerLimit()) {
                        throw new GuiguException(ResultCodeEnum.COUPON_USER_LIMIT);
                    }
                }

                //5、更新优惠券领取数量
                int row = 0;
                if (couponInfo.getPublishCount() == 0) {//没有限制
                    row = couponInfoMapper.updateReceiveCount(couponId);
                } else {
                    row = couponInfoMapper.updateReceiveCountByLimit(couponId);
                }
                if (row == 1) {
                    //6、保存领取记录
                    this.saveCustomerCoupon(customerId, couponId, couponInfo.getExpireTime());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != lock) {
                lock.unlock();
            }
        }
        throw new GuiguException(ResultCodeEnum.COUPON_LESS);
    }

    /**
     * 保存用户优惠券信息
     *
     * @param customerId 用户ID，用于关联用户信息
     * @param couponId 优惠券ID，标识优惠券的类型或来源
     * @param expireTime 过期时间，标记优惠券的有效期限
     * 此方法用于创建用户领取优惠券的记录，包括关联用户、优惠券信息，并设置初始状态为有效，记录领取时间及过期时间
     */
    private void saveCustomerCoupon(Long customerId, Long couponId, Date expireTime) {
        // 创建用户优惠券实体对象
        CustomerCoupon customerCoupon = new CustomerCoupon();
        // 设置用户ID
        customerCoupon.setCustomerId(customerId);
        // 设置优惠券ID
        customerCoupon.setCouponId(couponId);
        // 设置优惠券状态，假设1代表未使用
        customerCoupon.setStatus(1);
        // 设置优惠券领取时间
        customerCoupon.setReceiveTime(new Date());
        // 设置优惠券过期时间
        customerCoupon.setExpireTime(expireTime);
        // 插入用户优惠券记录到数据库
        customerCouponMapper.insert(customerCoupon);
    }


    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, BigDecimal orderAmount) {
        //1.定义符合条件的优惠券信息容器
        List<AvailableCouponVo> availableCouponVoList = new ArrayList<>();

        //2.获取未使用的优惠券列表
        List<NoUseCouponVo> list = couponInfoMapper.findNoUseList(customerId);
        //2.1.现金券
        List<NoUseCouponVo> type1List = list.stream().filter(item -> item.getCouponType().intValue() == 1).collect(Collectors.toList());
        for (NoUseCouponVo noUseCouponVo : type1List) {
            //使用门槛判断
            //2.1.1.没门槛，订单金额必须大于优惠券减免金额
            //减免金额
            BigDecimal reduceAmount = noUseCouponVo.getAmount();
            if (noUseCouponVo.getConditionAmount().doubleValue() == 0 && orderAmount.subtract(reduceAmount).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
            //2.1.2.有门槛，订单金额大于优惠券门槛金额
            if (noUseCouponVo.getConditionAmount().doubleValue() > 0 && orderAmount.subtract(noUseCouponVo.getConditionAmount()).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
        }

        //2.2.折扣券
        List<NoUseCouponVo> type2List = list.stream().filter(item -> item.getCouponType().intValue() == 2).collect(Collectors.toList());
        for (NoUseCouponVo noUseCouponVo : type2List) {
            //使用门槛判断
            //订单折扣后金额
            BigDecimal discountOrderAmount = orderAmount.multiply(noUseCouponVo.getDiscount()).divide(new BigDecimal("10")).setScale(2, RoundingMode.HALF_UP);
            //减免金额
            BigDecimal reduceAmount = orderAmount.subtract(discountOrderAmount);
            //订单优惠金额
            //2.2.1.没门槛
            if (noUseCouponVo.getConditionAmount().doubleValue() == 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
            //2.2.2.有门槛，订单折扣后金额大于优惠券门槛金额
            if (noUseCouponVo.getConditionAmount().doubleValue() > 0 && discountOrderAmount.subtract(noUseCouponVo.getConditionAmount()).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
        }

        //排序
        if (!CollectionUtils.isEmpty(availableCouponVoList)) {
            Collections.sort(availableCouponVoList, new Comparator<AvailableCouponVo>() {
                @Override
                public int compare(AvailableCouponVo o1, AvailableCouponVo o2) {
                    return o1.getReduceAmount().compareTo(o2.getReduceAmount());
                }
            });
        }
        return availableCouponVoList;
    }

    /**
     * 构建最佳未使用优惠券信息
     * 此方法用于将选定的未使用优惠券信息转换为可用优惠券信息对象，并设置优惠券ID和减免金额
     *
     * @param noUseCouponVo 未使用的优惠券信息
     * @param reduceAmount 减免的金额，表示使用优惠券后减少的消费额
     * @return 返回转换后的最佳未使用优惠券信息对象
     */
    private AvailableCouponVo buildBestNoUseCouponVo(NoUseCouponVo noUseCouponVo, BigDecimal reduceAmount) {
        // 创建一个AvailableCouponVo对象实例，用于存储最佳未使用的优惠券信息
        AvailableCouponVo bestNoUseCouponVo = new AvailableCouponVo();
        // 复制未使用优惠券的基本属性到AvailableCouponVo对象中
        BeanUtils.copyProperties(noUseCouponVo, bestNoUseCouponVo);
        // 设置优惠券的ID，确保优惠券信息的完整性
        bestNoUseCouponVo.setCouponId(noUseCouponVo.getId());
        // 设置减免金额，表示该优惠券可以为用户节省的金额
        bestNoUseCouponVo.setReduceAmount(reduceAmount);
        // 返回填充完整的AvailableCouponVo对象，代表最佳未使用的优惠券信息
        return bestNoUseCouponVo;
    }

}
