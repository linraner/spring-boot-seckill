package com.lin.seckill.rabbitmq;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderExpireMessage {
    private String expireKey;
}
