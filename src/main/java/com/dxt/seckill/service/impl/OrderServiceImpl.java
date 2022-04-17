package com.dxt.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxt.seckill.exception.GlobalException;
import com.dxt.seckill.mapper.OrderMapper;
import com.dxt.seckill.pojo.Order;
import com.dxt.seckill.pojo.SeckillGoods;
import com.dxt.seckill.pojo.SeckillOrder;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IGoodsService;
import com.dxt.seckill.service.IOrderService;
import com.dxt.seckill.service.ISeckillGoodsService;
import com.dxt.seckill.service.ISeckillOrderService;
import com.dxt.seckill.utils.MD5Util;
import com.dxt.seckill.utils.UUIDUtil;
import com.dxt.seckill.vo.GoodsVo;
import com.dxt.seckill.vo.OrderDetailVo;
import com.dxt.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        //秒杀商品库存-1
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1").eq("goods_id", goods.getId()));

        //判断是否还有库存
        if (seckillGoods.getStockCount() < 1){
            //将空库存信息存入redis
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        return new OrderDetailVo(order, goodsVo);
    }

    //获取秒杀地址
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    //校验秒杀地址
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    //验证码校验
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }
        String redisCaptcha = (String)redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
