package com.lin.seckill.common.access;

import com.lin.seckill.model.User;

public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUserHolder(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

    private UserContext() {

    }

}
