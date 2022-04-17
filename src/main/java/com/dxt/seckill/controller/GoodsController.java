package com.dxt.seckill.controller;

import com.dxt.seckill.pojo.User;
import com.dxt.seckill.service.IGoodsService;
import com.dxt.seckill.service.IUserService;
import com.dxt.seckill.vo.DetailVo;
import com.dxt.seckill.vo.GoodsVo;
import com.dxt.seckill.vo.RespBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 编写/goods,{/toList,/toDetail2/{goodsId},/detail/{goodsId}}的逻辑代码
 * @Author : dxt
 * @Date 2022/3/12 16:00
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    IUserService userService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        //如果缓存中有页面
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        //如果缓存中没有页面，手动渲染页面
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);

        //渲染成功
        if(!StringUtils.isEmpty(html)){
            //将页面存入redis缓存，将该页面加上一个失效时间1min
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }

        return html;
    }




    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int secKillStatus = 0;

        //秒杀倒计时
        int remainSeconds = 0;

        //秒杀未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime())/1000);
            //secKillStatus = 0;
        }
        //秒杀已结束
        else if (nowDate.after(endDate)){
            secKillStatus = 2;
        }
        //秒杀进行中
        else{
            secKillStatus = 1;
        }

        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);

        //如果缓存中没有页面，手动渲染页面
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);

        //渲染成功
        if(!StringUtils.isEmpty(html)){
            //将页面存入redis缓存，将该页面加上一个失效时间1min
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }

        return html;
    }




    @RequestMapping( "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int secKillStatus = 0;

        //秒杀倒计时
        int remainSeconds = 0;

        //秒杀未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime())/1000);
            //secKillStatus = 0;
        }
        //秒杀已结束
        else if (nowDate.after(endDate)){
            secKillStatus = 2;
        }
        //秒杀进行中
        else{
            secKillStatus = 1;
        }

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);

    }


}
