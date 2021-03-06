package com.lin.seckill.web;

import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.domain.OrderInformation;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.redis.OrderKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IGoodsService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.service.IUserService;
import com.lin.seckill.vo.GoodsVO;
import com.lin.seckill.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    @ResponseBody
    public Result<OrderDetailVO> list(User user, @RequestParam("orderId") long orderId) {
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

    /**
     * 测试接口
     * @param user
     * @return
     */
    @GetMapping("/payAll")
    public String payOrder(User user) {
        if (user == null) {
            return "redirect:/login";
        }
        List<SeckillOrder> seckillOrders = orderService.getSeckillOrderList();
        for (SeckillOrder seckillOrder : seckillOrders) {
            Long userId = seckillOrder.getUserId();
            Long goodsId = seckillOrder.getGoodsId();
            Long orderId = seckillOrder.getOrderId();
            int status = seckillOrder.getStatus();

            SeckillOrder tSeckillOrder = redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
            if (tSeckillOrder == null) {
                // 缓存失效
            } else {
                orderService.updatePayOrderInformation(seckillOrder);
            }

        }

        return "pay all ok";
    }
}
