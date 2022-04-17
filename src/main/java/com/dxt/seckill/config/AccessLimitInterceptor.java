package com.dxt.seckill.config;

import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IUserService;
import com.dxt.seckill.utils.CookieUtil;
import com.dxt.seckill.vo.RespBean;
import com.dxt.seckill.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @Author : dxt
 * @Date 2022/3/25 17:03
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            User user = getUser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key += ":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer)valueOperations.get(key);
            if (count == null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if (count < maxCount){
                valueOperations.increment(key);
            }else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }


    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/jason");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }


    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)){
            return null;
        }
        return userService.getUserByCookie(ticket, request, response);
    }

}
