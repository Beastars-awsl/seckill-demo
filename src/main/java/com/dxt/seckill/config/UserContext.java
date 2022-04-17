package com.dxt.seckill.config;

import com.dxt.seckill.pojo.User;

/**
 * @Author : dxt
 * @Date 2022/3/25 17:18
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
