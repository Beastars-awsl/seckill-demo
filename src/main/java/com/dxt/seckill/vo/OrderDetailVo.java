package com.dxt.seckill.vo;

import com.dxt.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : dxt
 * @Date 2022/3/19 21:32
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {

    private Order order;

    private GoodsVo goodsVo;
}
