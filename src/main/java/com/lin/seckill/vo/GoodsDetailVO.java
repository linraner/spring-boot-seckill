package com.lin.seckill.vo;

import com.lin.seckill.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailVO {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVO goods;
    private User user;
}
