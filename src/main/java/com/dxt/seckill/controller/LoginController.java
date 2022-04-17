package com.dxt.seckill.controller;

import com.dxt.seckill.service.IUserService;
import com.dxt.seckill.utils.MD5Util;
import com.dxt.seckill.vo.LoginVo;
import com.dxt.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    //登录功能
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        //log.info(MD5Util.fromPassToDBPass(loginVo.getPassword(),"1a2b3c4d"));
        return userService.doLogin(loginVo, request, response);
    }



}
