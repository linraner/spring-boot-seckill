package com.lin.seckill.rabbitmq;

import com.lin.seckill.model.User;
import lombok.Data;

@Data
public class SeckillMessage {
    private User user;
    private long goodId;
}
