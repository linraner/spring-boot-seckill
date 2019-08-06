package com.lin.seckill.rabbitmq;

import com.lin.seckill.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

//    /**
//     * test
//     * @param message
//     */
//    public void send(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("sender message:{}", msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }


}
