package com.dxt.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author : dxt
 * @Date 2022/3/10 20:25
 */
@Getter
@ToString
@AllArgsConstructor
public enum  RespBeanEnum {

    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),

    //登录模块
    LOGIN_ERROR(500210, "用户名或密码不正确"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BIND_ERROR(500212, "参数校验异常"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "更新密码失败"),
    SESSION_ERROR(500215, "用户不存在"),

    //订单模块
    ORDER_NOT_EXIST(500300, "订单信息不存在"),

    //秒杀模块
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件"),
    REQUEST_ILLEGAL(500502, "非法请求"),
    ERROR_CAPTCHA(500503, "验证码错误"),
    ACCESS_LIMIT_REACHED(500504, "访问过于频繁");

    private final Integer code;
    private final String message;

    //订单模块
}
