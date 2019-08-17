package com.lin.seckill.timeTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Slf4j
@Component
public class SubscriberRedisKeyTimeout implements CommandLineRunner {

    @Resource
    private KeyExpiredListener keyExpiredListener;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void run(String... strings) throws Exception {
        Jedis jedis = jedisPool.getResource();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("KeyExpiredListener start");
                    jedis.subscribe(keyExpiredListener, "__keyevent@0__:expired");

                    log.info("orderKeyExpiredListener over");
                } catch (Exception e) {
                    log.info("KeyExpiredListener " + e.getMessage());
                }
            }
        }).start();

    }
}
