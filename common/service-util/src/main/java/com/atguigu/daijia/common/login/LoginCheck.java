package com.atguigu.daijia.common.login;

import java.lang.annotation.*;

/**
 * 登录校验注解，标识需要进行登录的方法
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginCheck {
}
