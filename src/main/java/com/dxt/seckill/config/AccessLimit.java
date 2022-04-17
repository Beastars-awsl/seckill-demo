package com.dxt.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author : dxt
 * @Date 2022/3/25 17:01
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int second();
    int maxCount();
    boolean needLogin() default true;

}
