package com.dxt.seckill.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dxt.seckill.pojo.Goods;
import com.dxt.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dxt
 * @since 2022-03-14
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    //获取商品列表
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
