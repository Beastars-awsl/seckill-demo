package com.dxt.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : dxt
 * @Date 2022/3/24 13:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {

    private User user;
    private Long GoodsId;
}
