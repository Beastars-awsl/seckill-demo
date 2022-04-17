package com.dxt.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxt.seckill.mapper.SeckillOrderMapper;
import com.dxt.seckill.pojo.SeckillOrder;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //获取秒杀结果， -1代表秒杀失败， 0代表处于消息队列中
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        //如果存在该用户对于该商品的秒杀订单，返回订单号 (即秒杀成功)
        if (null != seckillOrder){
            return seckillOrder.getOrderId();
        }else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else {
            return 0L;
        }
    }
}
