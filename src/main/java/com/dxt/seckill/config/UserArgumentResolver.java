package com.dxt.seckill.config;

import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IUserService;
import com.dxt.seckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解决每一层接口都需要进行参数校验的问题
 * @Author : dxt
 * @Date 2022/3/14 13:47
 */

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    IUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
            throws Exception {

//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
//        if(StringUtils.isEmpty(ticket)){
//            return null;
//        }
//        return userService.getUserByCookie(ticket, request, response);
        return UserContext.getUser();
    }
}
