package com.dxt.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dxt.seckill.pojo.Order;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.vo.GoodsVo;
import com.dxt.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
public interface IOrderService extends IService<Order> {
    Order secKill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    //获取秒杀地址
    String createPath(User user, Long goodsId);

    //校验秒杀地址
    boolean checkPath(User user, Long goodsId, String path);


    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
