package com.dxt.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dxt.seckill.config.AccessLimit;
import com.dxt.seckill.config.rabbitmq.MQSender;
import com.dxt.seckill.exception.GlobalException;
import com.dxt.seckill.pojo.Order;
import com.dxt.seckill.pojo.SeckillMessage;
import com.dxt.seckill.pojo.SeckillOrder;
import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IGoodsService;
import com.dxt.seckill.service.IOrderService;
import com.dxt.seckill.service.ISeckillOrderService;
import com.dxt.seckill.utils.JsonUtil;
import com.dxt.seckill.vo.GoodsVo;
import com.dxt.seckill.vo.RespBean;
import com.dxt.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author : dxt
 * @Date 2022/3/16 15:43
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    @RequestMapping("/doSeckill2")
    public String doSecKill2(Model model, User user, Long goodsId){


        if(user == null){
            return "login";
        }


        model.addAttribute("user", user);

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);

        //????????????????????????
        if (goods.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        //??????????????????????????????
        //mybatis plus ?????????????????????????????????(????????????ID?????????ID?????????)
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }


        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";

    }



    //????????????
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){

        ValueOperations valueOperations = redisTemplate.opsForValue();

        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }


//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //????????????????????????
//        if (goods.getStockCount() < 1){
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //??????????????????????????????
//        //mybatis plus ?????????????????????????????????(????????????ID?????????ID?????????)
//        //SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        //??????redis????????????
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
//
//        if (seckillOrder != null){
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//
//
//        Order order = orderService.secKill(user, goods);
//
//        return RespBean.success(order);

        //??????????????????????????????
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        //????????????
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        //????????????????????????redis?????????
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //??????????????????????????????????????????
        //Long stock = valueOperations.decrement("secKillGoods:" + goodsId);
        //?????????????????????????????????lua????????????redis???????????????????????????????????????decrement????????????????????????
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("secKillGoods:" + goodsId), Collections.EMPTY_LIST);
        //????????????0(???-1)?????????????????????
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            //???????????????0,?????????????????????
            valueOperations.increment("secKillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));

        return RespBean.success(0);


    }





    //????????????????????? -1????????????????????? 0???????????????????????????
    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long result = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(result);
    }





    //??????????????????
    @AccessLimit(second=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();

        if (!orderService.checkCaptcha(user, goodsId, captcha)){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }

        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }





    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-control", "no-cache");
        response.setDateHeader("Expires",0);
        //???????????????
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????", e.getMessage());
        }
    }




    //???????????????????????????????????????????????????InitializingBean?????????
    //??????????????????????????????????????????????????????????????????redis???????????????????????????????????????????????????
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("secKillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
