package com.lin.seckill.service;

import com.lin.seckill.domain.OrderInformation;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.vo.GoodsVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IOrderService {

    /**
     * 获取订单 userId+goodId
     * @param userId
     * @param goodId
     * @return
     */
    SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodId);


    /**
     * 根据orderID查询订单
     * @param orderId
     * @return
     */
    OrderInformation getOrderById(long orderId);


    /**
     * 创建订单信息 用户信息 和 商品信息
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    OrderInformation createOder(User user, GoodsVO goods);


    /**
     * 支付
     */
    @Transactional
    void updatePayOrderInformation(SeckillOrder seckillOrder);


    void restore(long goodsId);

    /**
     * 内部调用 删除订单接口
     */
    void deleteOrders();

    /**
     * 内部调用
     */
    List<SeckillOrder> getSeckillOrderList();
}
