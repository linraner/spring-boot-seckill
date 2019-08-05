package com.lin.seckill.service.impl;

import com.lin.seckill.dao.OrderDAO;
import com.lin.seckill.model.OrderInformation;
import com.lin.seckill.model.SeckillOrder;
import com.lin.seckill.model.User;
import com.lin.seckill.pojo.vo.GoodsVO;
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
    public OrderInformation createOderInformation(User user, GoodsVO goods) {
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setCreateDate(new Date());
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
        orderDAO.insertMiaoshaOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);
        return orderInformation;
    }

    @Override
    public void deleteOrders() {
        orderDAO.deleteOrders();
        orderDAO.deleteMiaoshaOrders();
    }
}
