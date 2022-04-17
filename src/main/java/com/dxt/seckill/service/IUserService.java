package com.dxt.seckill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.vo.LoginVo;
import com.dxt.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dxt
 * @since 2022-03-10
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    RespBean updatePassword(String userTicket, Long id, String password, HttpServletRequest request, HttpServletResponse response);

}
