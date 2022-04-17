package com.dxt.seckill.config.rabbitmq;

import com.dxt.seckill.pojo.SeckillMessage;
import com.dxt.seckill.pojo.SeckillOrder;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IGoodsService;
import com.dxt.seckill.service.IOrderService;
import com.dxt.seckill.utils.JsonUtil;
import com.dxt.seckill.vo.GoodsVo;
import com.dxt.seckill.vo.RespBean;
import com.dxt.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author : dxt
 * @Date 2022/3/22 20:51
 */

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息: " + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        if (goodsVo.getStockCount() < 1){
            return;
        }

        //判断用户是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder)  redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        //重复抢购
        if (seckillOrder != null){
            return;
        }

        orderService.secKill(user, goodsVo);
    }

}
