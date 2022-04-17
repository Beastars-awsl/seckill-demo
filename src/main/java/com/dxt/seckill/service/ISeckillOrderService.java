package com.dxt.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dxt.seckill.pojo.SeckillOrder;
import com.dxt.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
