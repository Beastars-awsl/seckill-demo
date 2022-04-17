package com.dxt.seckill.controller;

import com.dxt.seckill.pojo.Order;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IOrderService;
import com.dxt.seckill.vo.OrderDetailVo;
import com.dxt.seckill.vo.RespBean;
import com.dxt.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
