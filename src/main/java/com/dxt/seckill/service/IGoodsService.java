package com.dxt.seckill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dxt.seckill.pojo.Goods;
import com.dxt.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
public interface IGoodsService extends IService<Goods> {

    //获取商品列表
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
