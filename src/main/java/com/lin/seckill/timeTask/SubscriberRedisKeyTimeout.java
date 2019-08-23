package com.lin.seckill.timeTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

//@Slf4j
//@Component
public class SubscriberRedisKeyTimeout implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(SubscriberRedisKeyTimeout.class);

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
                    logger.info("KeyExpiredListener start");
                    jedis.subscribe(keyExpiredListener, "__keyevent@0__:expired");

                    logger.info("orderKeyExpiredListener over");
                } catch (Exception e) {
                    logger.info("KeyExpiredListener " + e.getMessage());
                }
            }
        }).start();

    }
}
