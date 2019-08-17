package com.lin.seckill.rabbitmq;

import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.redis.OrderKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IGoodsService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.service.ISeckillService;
import com.lin.seckill.vo.GoodsVO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class MQReceiver {
    @Autowired
    RedisService redisService;

    @Autowired
    IGoodsService goodsService;

    @Autowired
    IOrderService orderService;

    @Autowired
    ISeckillService seckillService;

    /**
     * 直接消费队列任务
     *
     * @param message
     */
    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SeckillOrderMessage mm = RedisService.stringToBean(message, SeckillOrderMessage.class);
        User user = mm.getUser();
        long goodsId = mm.getGoodId();

        GoodsVO goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goods);
    }

    /**
     * 处理过期key
     *
     * @param message
     */
    @RabbitListener(queues = MQConfig.ORDER_EXPIRE_QUEUE)
    public void receiveOrderExpireMessage(String message) {
        log.info("order receive:" + message);
        OrderExpireMessage msg = RedisService.stringToBean(message, OrderExpireMessage.class);
        String expireKey = msg.getExpireKey();
        // 对失效订单的处理 以下这个方法并不好
        if (expireKey.startsWith(OrderKey.getSeckillOrderByUidGidPer.getPrefix())) {
            expireKey = expireKey.substring(OrderKey.getSeckillOrderByUidGidPer.getPrefix().length());
            int split = expireKey.indexOf("_");

            Long userId = Long.valueOf(expireKey.substring(0, split));
            Long goodsId = Long.valueOf(expireKey.substring(split + 1));

            // 回滚库存
            orderService.restore(goodsId);

            // todo: mysql刷新未支付订单

        }

    }

    @RabbitListener(queues = {MQConfig.REGISTER_QUEUE_NAME})
    public void listenerDelayQueue(SeckillOrder orderMessage, Message message, Channel channel) {
        log.info("[listenerDelayQueue 监听的消息] - [消费时间] - [{}] - [{}]", LocalDateTime.now(), seckillService.toString());
        try {
            // TODO 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            // TODO 如果报错了,那么我们可以进行容错处理,比如转移当前消息进入其它队列
        }
    }


    @RabbitListener(queues = MQConfig.QUEUE)
    public void receivetest(String message) {
        log.info("receive message: {}", message);
    }
}
