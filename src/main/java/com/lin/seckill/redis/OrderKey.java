package com.lin.seckill.redis;

public class OrderKey extends BasePrefix {
    public final static int ORDER_EXPIRE = 10 * 60;

    public static OrderKey getSeckillOrderByUidGid = new OrderKey(ORDER_EXPIRE,"moug");
    public static OrderKey getSeckillOrderByUidGidPer = new OrderKey(0,"moug");

    OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


}
