package com.lin.seckill.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisServiceTest {

    @Autowired
    RedisService redisService;
    private java.lang.Object Object;

    @Test
    public void get() {
        String pre = SeckillKey.isGoodOver.getPrefix();
        System.out.println(pre);
        RedisConfig redisConfig = new RedisConfig();
        System.out.println(redisConfig.getHost() + " " + redisConfig.getPort());
    }

    @Test
    public void set() {
        boolean flag = redisService.set(SeckillKey.isGoodOver, "" + "1000", true);
        System.out.println(flag);

    }

    @Test
    public void exists() {
        boolean flag = redisService.exists(SeckillKey.isGoodOver, "" + "1000");
        System.out.println(flag);
    }

    @Test
    public void delete() {
        redisService.delete(SeckillKey.isGoodOver, "" + "1000");
    }

    @Test
    public void incr() {
    }

    @Test
    public void decr() {
    }

    @Test
    public void delete1() {
    }

    @Test
    public void beanToString() {
    }

    @Test
    public void stringToBean() {
    }
}