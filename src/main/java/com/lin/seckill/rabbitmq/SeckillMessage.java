package com.lin.seckill.rabbitmq;

import com.lin.seckill.domain.User;
import lombok.Data;

@Data
public class SeckillMessage {
    private User user;
    private long goodId;
}
