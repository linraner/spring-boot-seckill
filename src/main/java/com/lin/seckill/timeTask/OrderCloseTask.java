package com.lin.seckill.timeTask;

import com.lin.seckill.common.result.Result;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.redis.SeckillKey;
import com.lin.seckill.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单未支付5分钟 回退库存
 */
public class OrderCloseTask {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IOrderService orderService;


    // 从缓存中拿取订单
    public Result<Integer> orderClose(long userId, long goodsId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        // 回退操作
        SeckillOrder order = new SeckillOrder();

        return Result.success(0);
    }


    // 定时任务

}
