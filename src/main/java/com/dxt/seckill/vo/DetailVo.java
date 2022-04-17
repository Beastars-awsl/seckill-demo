package com.dxt.seckill.vo;

import com.dxt.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情返回对象
 * @Author : dxt
 * @Date 2022/3/19 10:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private User user;

    private GoodsVo goodsVo;

    private int secKillStatus;

    private int remainSeconds;
}
