package com.lin.seckill.redis;

public class UserKey extends BasePrefix {
    private static final int TOKEN_EXPIRE = 24*60*3600;
    public static UserKey token = new UserKey(TOKEN_EXPIRE, "tk");
    public static UserKey getById = new UserKey(0, "id");

    UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
