package com.lin.seckill.redis;

public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds;
    private String prefix;

    BasePrefix(String prefix) {
        this(0, prefix);
    }

    BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds() {
        return expireSeconds;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
