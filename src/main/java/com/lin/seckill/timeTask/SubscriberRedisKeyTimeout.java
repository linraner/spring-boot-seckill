package com.lin.seckill.timeTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Slf4j
@Component
public class SubscriberRedisKeyTimeout implements CommandLineRunner {

    @Resource
    private OrderKeyExpiredListener orderKeyExpiredListener;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void run(String... strings) throws Exception {
        Jedis jedis = jedisPool.getResource();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("orderKeyExpiredListener start");
                    jedis.subscribe(orderKeyExpiredListener, "__keyevent@0__:expired");

                    log.info("orderKeyExpiredListener over");
                } catch (Exception e) {
                    log.info("orderKeyExpiredListener " + e.getMessage());
                }
            }
        }).start();

    }
}
