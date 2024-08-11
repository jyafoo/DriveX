package com.atguigu.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.atguigu.daijia.customer.mapper.CustomerInfoMapper;
import com.atguigu.daijia.customer.mapper.CustomerLoginLogMapper;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    private final WxMaService wxMaService;
    private final CustomerInfoMapper customerInfoMapper;
    private final CustomerLoginLogMapper customerLoginLogMapper;
    private final RedisTemplate redisTemplate;

    @Override
    public Long login(String code) {
        // 1 获取code值，使用微信工具包对象，获取微信唯一标识openId
        String openId = null;
        try {
            WxMaJscode2SessionResult sessionInfo =
                    wxMaService.getUserService().getSessionInfo(code);
            openId = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

        // 2 根据openid查询数据库表，判断是否第一次登录
        // 如果openid不存在返回null，如果存在返回一条记录
        // select * from customer_info ci where ci.wx_open_id =
        LambdaQueryWrapper<CustomerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerInfo::getWxOpenId, openId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        // 3 如果第一次登录，添加信息到用户表
        if (customerInfo == null) {
            customerInfo = CustomerInfo.builder()
                    .nickname(String.valueOf(System.currentTimeMillis()))
                    .avatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg")
                    .wxOpenId(openId).build();

            customerInfoMapper.insert(customerInfo);
        }

        // 4记录登录日志信息
        CustomerLoginLog loginLog = CustomerLoginLog.builder()
                .customerId(customerInfo.getId())
                .msg("小程序用户登录")
                .build();
        customerLoginLogMapper.insert(loginLog);

        // 5返回用户id
        return customerInfo.getId();
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        // 1 根据用户id查询用户信息
        CustomerInfo customerInfo = customerInfoMapper.selectById(customerId);

        // redisTemplate.opsForValue().set("adf","adsfadsfds");
        // 2 封装到customerLoginVo
        CustomerLoginVo customerLoginVo = new CustomerLoginVo();
        BeanUtils.copyProperties(customerInfo, customerLoginVo);

        // @schema(description="是否绑定手机号码")
        // private Boolean isBindPhone
        customerLoginVo.setIsBindPhone(StringUtils.hasText(customerInfo.getPhone()));

        // 3 CustomerLoginVo返回
        return customerLoginVo;
    }
}
