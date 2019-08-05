package com.lin.seckill.service;

import com.lin.seckill.model.OrderInformation;
import com.lin.seckill.model.SeckillOrder;
import com.lin.seckill.model.User;
import com.lin.seckill.pojo.vo.GoodsVO;
import org.springframework.transaction.annotation.Transactional;

public interface IOrderService {

    /**
     *
     * @param userId
     * @param goodId
     * @return
     */
    SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodId);


    /**
     *
     * @param orderId
     * @return
     */
    OrderInformation getOrderById(long orderId);


    /**
     * 创建订单信息
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    OrderInformation createOderInformation(User user, GoodsVO goodsVO);


    /**
     * 内部调用
     */
    void deleteOrders();
}
