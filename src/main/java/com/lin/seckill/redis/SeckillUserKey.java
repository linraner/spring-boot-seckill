package com.lin.seckill.redis;

public class SeckillUserKey extends BasePrefix {
    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;
    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
    public static SeckillUserKey getById = new SeckillUserKey(0, "id");
    SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
