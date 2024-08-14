package com.atguigu.daijia.common.login;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/11
 */
@Component("loginAspect")
@Aspect
@Slf4j
@Order(100)
public class LoginAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 校验登录token
     *
     * @param joinPoint
     * @param loginCheck
     * @return
     * @throws Throwable
     */
    @Around("@annotation(loginCheck)")
    public Object process(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        // 1、获取请求头
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String token = request.getHeader("token");

        if (!StringUtils.hasText(token)) {
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }

        String userId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        if (StringUtils.hasText(userId)) {
            AuthContextHolder.setUserId(Long.parseLong(userId));
        }

        // 调用原始方法
        Object res = joinPoint.proceed();

        AuthContextHolder.removeUserId();

        return res;
    }
}
