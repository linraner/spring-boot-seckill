package com.lin.seckill.service.impl;

import com.lin.seckill.dao.OrderDAO;
import com.lin.seckill.domain.OrderInformation;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.rabbitmq.MQConfig;
import com.lin.seckill.redis.GoodsKey;
import com.lin.seckill.redis.OrderKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {
    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private RedisService redisService;

    public OrderServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

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
        // 延迟队列
        delaySeckillOrderMessage(seckillOrder);

        return orderInformation;
    }

    /**
     * 支付订单 status = 1
     *
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

    @Override
    public List<SeckillOrder> getSeckillOrderList() {
        return orderDAO.getSeckillOrderList();
    }

    private void delaySeckillOrderMessage(SeckillOrder order) {
        // 添加延时队列
        this.rabbitTemplate.convertAndSend(MQConfig.ORDER_DELAY_QUEUE, MQConfig.DELAY_ROUTING_KEY, order, message -> {
            // 第一句是可要可不要,根据自己需要自行处理
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, SeckillOrder.class.getName());
            // 如果配置了 params.put("x-message-ttl", 5 * 1000); 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
            message.getMessageProperties().setExpiration(5 * 1000 + "");
            return message;
        });
        log.info("[发送时间] - [{}]", LocalDateTime.now());
    }
}
