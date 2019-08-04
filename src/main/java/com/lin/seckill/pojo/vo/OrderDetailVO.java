package com.lin.seckill.pojo.vo;

import com.lin.seckill.model.OrderInformation;
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
