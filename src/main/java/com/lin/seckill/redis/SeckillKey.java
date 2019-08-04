package com.lin.seckill.redis;

public class SeckillKey extends BasePrefix {
    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey isGoodOver = new SeckillKey(0, "go");
    public static SeckillKey getSeckillPath = new SeckillKey(60, "mp");
    public static SeckillKey getSeckillVerifyCode = new SeckillKey(300, "vc");
}
