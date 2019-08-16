package com.lin.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 用于缓存的秒杀订单信息 创建时间 支付时间 支付状态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeckillOrder {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;

//    private Date createDate;
//    private Date payDate;
//    private Integer status;
}
