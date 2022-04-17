package com.dxt.seckill.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxt.seckill.exception.GlobalException;
import com.dxt.seckill.mapper.UserMapper;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IUserService;
import com.dxt.seckill.utils.CookieUtil;
import com.dxt.seckill.utils.MD5Util;
import com.dxt.seckill.utils.UUIDUtil;
import com.dxt.seckill.vo.LoginVo;
import com.dxt.seckill.vo.RespBean;
import com.dxt.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dxt
 * @since 2022-03-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        //如果用户名或密码为空
//        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//
//        //如果手机号校验不通过
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if(null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //判断密码是否输入正确
        if(!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user" + ticket, user);
        //request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request,response,"userTicket", ticket);

        return RespBean.success();
    }



    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(userTicket == null){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user" + userTicket);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, Long id, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(user == null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        //更新密码成功
        if(1 == result){
            //删除redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
