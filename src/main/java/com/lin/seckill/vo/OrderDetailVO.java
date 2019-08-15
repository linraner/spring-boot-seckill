package com.lin.seckill.vo;

import com.lin.seckill.domain.OrderInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVO {
    private GoodsVO goodsVO;
    private OrderInformation order;
}
