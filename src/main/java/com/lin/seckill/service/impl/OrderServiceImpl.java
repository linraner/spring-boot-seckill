package com.lin.seckill.service.impl;

import com.lin.seckill.dao.OrderDAO;
import com.lin.seckill.domain.OrderInformation;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.redis.GoodsKey;
import com.lin.seckill.vo.GoodsVO;
import com.lin.seckill.redis.OrderKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private RedisService redisService;

    @Override
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodId, SeckillOrder.class);
    }

    @Override
    public OrderInformation getOrderById(long orderId) {
        return orderDAO.getOrderById(orderId);
    }

    @Override
    public OrderInformation createOder(User user, GoodsVO goods) {
        /**
         * todo: 此处应该有一个订单ID生成规则
         */
        Date createDate = new Date();
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setCreateDate(createDate);
        orderInformation.setDeliveryAddrId(0L);
        orderInformation.setGoodsCount(1);
        orderInformation.setGoodsId(goods.getId());
        orderInformation.setGoodsName(goods.getGoodsName());
        orderInformation.setGoodsPrice(goods.getMiaoshaPrice());
        orderInformation.setOrderChannel(1);
        orderInformation.setStatus(0);
        orderInformation.setUserId(user.getId());
        orderDAO.insert(orderInformation);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInformation.getId());

        seckillOrder.setUserId(user.getId());
        seckillOrder.setStatus(0);
        orderDAO.insertMiaoshaOrder(seckillOrder);
        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);

        return orderInformation;
    }

    /**
     * 支付订单 status = 1
     * @param seckillOrder
     */
    @Override
    public void updatePayOrderInformation(SeckillOrder seckillOrder) {
        Long orderId = seckillOrder.getOrderId();
        Date payDate = new Date();
        // redis更新
        seckillOrder.setStatus(1);
        redisService.set(OrderKey.getSeckillOrderByUidGidPer, "" + seckillOrder.getUserId() + "_" + seckillOrder.getGoodsId(), seckillOrder);
        // mysql更新
        orderDAO.updatePayOrderInformation(orderId, payDate);
    }


    /**
     * 未支付 时间超时 回库
     */
    public void restore(long goodsId) {
        redisService.incr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        // todo: mysql 秒杀结束之后刷入库存 如果是未支付的订单 在下次访问时候再删除或者更新订单

    }



    @Override
    public void deleteOrders() {

        orderDAO.deleteOrders();
        orderDAO.deleteMiaoshaOrders();
    }
}
