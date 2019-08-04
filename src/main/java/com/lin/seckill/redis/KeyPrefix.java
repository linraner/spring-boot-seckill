package com.lin.seckill.redis;

public interface KeyPrefix {
    /**
     * key失效时间, 0代表永久不失效
     * @return
     */
    int expireSeconds();

    /**
     * 获取key前缀
     * @return
     */
    String getPrefix();
}
