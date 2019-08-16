package com.lin.seckill.redis;

public class OrderKey extends BasePrefix {
    OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey("moug");


}
