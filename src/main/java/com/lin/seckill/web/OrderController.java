package com.lin.seckill.web;

import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.model.OrderInformation;
import com.lin.seckill.model.SeckillUser;
import com.lin.seckill.pojo.vo.GoodsVO;
import com.lin.seckill.pojo.vo.OrderDetailVO;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IGoodsService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("/detail")
    public Result<OrderDetailVO> list(SeckillUser user, @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }

        OrderInformation order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMessage.ORDER_NOT_EXIST);
        }

        long goodsId = order.getGoodsId();
        GoodsVO goodsVO = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setGoodsVO(goodsVO);
        orderDetailVO.setOrder(order);
        return Result.success(orderDetailVO);
    }
}
