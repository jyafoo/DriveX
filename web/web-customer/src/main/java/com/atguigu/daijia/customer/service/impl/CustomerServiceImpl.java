package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerInfoFeignClient customerInfoFeignclient;
    private final RedisTemplate redisTemplate;

    @Override
    public String login(String code) {
        // 1 拿着code进行远程调用，返回用户id
        Result<Long> longResult = customerInfoFeignclient.login(code);
        Long customerId = longResult.getData();
        // 2 判断如果返回失败了或判断返回用户id是否为空，为空则返回错误提示
        if (longResult.getCode() != 200 || customerId == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        // 3 生成token字符串
        String token = UUID.randomUUID().toString().replace("-", "");

        // 4 把用户id放到Redis，设置过期时间
        // key:token  value:customerId
        // redisTemplate.opsForValue().set(token,customerId.tostring(),30, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                customerId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                TimeUnit.MINUTES);

        // 5 返回token
        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        Result<CustomerLoginVo> result = customerInfoFeignclient.getCustomerLoginInfo(customerId);
        if(result.getCode().intValue() != 200){
            throw new GuiguException(result.getCode(), result.getMessage());
        }

        CustomerLoginVo customerLoginVo = result.getData();
        if(customerLoginVo == null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        return customerLoginVo;
    }

}
