package com.lin.seckill.common.access;

import com.lin.seckill.model.SeckillUser;

public class UserContext {
    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUserHolder(SeckillUser user) {
        userHolder.set(user);
    }

    public static SeckillUser getUser() {
        return userHolder.get();
    }

    private UserContext() {

    }

}
