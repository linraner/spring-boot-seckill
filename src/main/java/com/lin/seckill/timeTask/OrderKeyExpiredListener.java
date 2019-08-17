package com.lin.seckill.timeTask;

import com.lin.seckill.rabbitmq.MQSender;
import com.lin.seckill.rabbitmq.OrderExpireMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Component
public class OrderKeyExpiredListener extends JedisPubSub  {

    @Autowired
    private MQSender mqSender;

    private Logger logger = LoggerFactory.getLogger(OrderKeyExpiredListener.class);

    @Override
    public void onMessage(String channel, String message) {
        // 过期key
        logger.info("OrderKeyExpiredListener: " + message);
        OrderExpireMessage orderExpireMessage = new OrderExpireMessage();
        orderExpireMessage.setExpireKey(message);
        mqSender.sendOrderExpireMessage(orderExpireMessage);

    }
}
