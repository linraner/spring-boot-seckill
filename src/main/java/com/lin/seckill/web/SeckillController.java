package com.lin.seckill.web;

import com.lin.seckill.common.access.AccessLimit;
import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.model.SeckillOrder;
import com.lin.seckill.model.User;
import com.lin.seckill.pojo.vo.GoodsVO;
import com.lin.seckill.rabbitmq.MQSender;
import com.lin.seckill.rabbitmq.SeckillMessage;
import com.lin.seckill.redis.GoodsKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IGoodsService;
import com.lin.seckill.service.IOrderService;
import com.lin.seckill.service.ISeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

@Slf4j
@Controller
@RequestMapping("/miaosha")
public class SeckillController {

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

    @PostMapping("/do_miaosha")
    public String list(Model model, User user, @RequestParam("goodsId") long goodsId) {
        log.info("秒杀界面用户信息: {} 商品ID: {}", user.toString(), goodsId);
        if (null == user) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVO goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMessage.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMessage.REPEATE_MIAOSHA.getMsg());
        }

        seckillService.seckill(user, goods);
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        log.info("秒杀用户:{}秒杀商品:{}结果:{}", user.getId(), goodsId, result);
        return "redirect:/goods_list";
    }


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

}
