package com.lin.seckill.web;

import com.lin.seckill.common.access.AccessLimit;
import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.rabbitmq.MQSender;
import com.lin.seckill.rabbitmq.SeckillMessage;
import com.lin.seckill.redis.*;
import com.lin.seckill.service.IGoodsService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.service.ISeckillService;
import com.lin.seckill.service.IUserService;
import com.lin.seckill.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequestMapping("/miaosha")
public class SeckillController implements InitializingBean {

    ConcurrentHashMap<Long, Boolean> seckillOverMap = new ConcurrentHashMap<Long, Boolean>();
    @Autowired
    private RedisService redisService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ISeckillService seckillService;
    @Autowired
    private MQSender sender;
    @Autowired
    private IUserService userService;

    @GetMapping("/reset")
    public Result<Boolean> reset() {
        List<GoodsVO> goodsList = goodsService.listGoodsVO();
        for (GoodsVO goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
        }
        redisService.delete(OrderKey.getSeckillOrderByUidGid);
        redisService.delete(SeckillKey.isGoodOver);
        seckillService.reset(goodsList);
        return Result.success(true);
    }

    @PostMapping("/{path}/do_miaosha")
    public Result<Integer> seckill(Model model, User user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }

        boolean checkPath = seckillService.checkPath(user, goodsId, path);
        if (!checkPath) {
            return Result.error(CodeMessage.REQUEST_ILLEGAL);
        }

        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            return Result.error(CodeMessage.MIAO_SHA_OVER);
        }

        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMessage.REPEATE_MIAOSHA);
        }

        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodId(goodsId);
        // 加入队列
        sender.sendSeckillMessage(seckillMessage);
        return Result.success(0);
    }

    /**
     * 用于测试的 秒杀接口
     */
    @PostMapping("/do_miaosha")
    @ResponseBody
    public String list(Model model, @RequestParam("token") String token, @RequestParam("goodsId") long goodsId,
                       @RequestParam("userId") long userId) {
        User user = redisService.get(UserKey.token, token, User.class);

        if (user == null) {
            log.info("用户为空");
            user = userService.getById(userId);
        }

        log.info("秒杀接口：正在秒杀--用户ID: {} 商品ID: {}", user.getId(), goodsId);
        // 用户检查
        if (user == null) {
            return "user is null";
        }

        // 提前标记
        boolean over = seckillOverMap.getOrDefault(goodsId, true);
        if (over) {
            return "is over";
        }

        // 库存检查
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            seckillOverMap.put(goodsId, true);
            return "stock < 0";
        }

        // 订单检查 redis检查
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            log.info("userId: {} order:{}", userId, order.getOrderId());
            return "order is not null";
        }

        // 秒杀任务队列
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodId(goodsId);
        // 加入队列
        sender.sendSeckillMessage(seckillMessage);

        return "seckill ok";
    }


    /**
     * 验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds = 5, maxCount = 5)//实现了接口防刷的功能
    @GetMapping("/path")
    public Result<String> getMiaoshaPath(User user, @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        if (user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMessage.REQUEST_ILLEGAL);
        }
        String path = seckillService.createSeckillPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 验证码校验
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/verifyCode")
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, User user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        try {
            BufferedImage bufferedImage = seckillService.createVerifyCode(user, goodsId);
            OutputStream outputStream = response.getOutputStream();
            //把图片写入到输出流中
            ImageIO.write(bufferedImage, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            //图片通过OutputStream返回出去了，不需要return
            return null;
        } catch (Exception e) {
            return Result.error(CodeMessage.MIAOSHA_FAIL);
        }
    }

    @Override
    public void afterPropertiesSet() {
        List<GoodsVO> goodsList = goodsService.listGoodsVO();
        if (goodsList == null) {
            return;
        }
        for (GoodsVO goods : goodsList) {
            //商品库存加载到redis中
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            seckillOverMap.put(goods.getId(), false);
        }
    }

}
