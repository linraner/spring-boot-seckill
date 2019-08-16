package com.lin.seckill.service.impl;

import com.lin.seckill.common.util.MD5Util;
import com.lin.seckill.common.util.UUIDUtil;
import com.lin.seckill.domain.SeckillOrder;
import com.lin.seckill.domain.User;
import com.lin.seckill.vo.GoodsVO;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.redis.SeckillKey;
import com.lin.seckill.service.ISeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class SeckillServiceImpl implements ISeckillService {

    private static char[] ops = new char[]{'+', '-', '*'};
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private OrderServiceImpl orderService;

    /**
     * 校验验证码
     *
     * @param exp
     * @return
     */
    private static int calc(String exp) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
            return (Integer) scriptEngine.eval(exp);
        } catch (Exception e) {
            log.error("验证码异常" + e.getMessage());
            return 0;
        }
    }

    /**
     * 生成验证码
     *
     * @param random
     * @return
     */
    private static String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        return "" + num1 + op1 + num2 + op2 + num3;
    }

    @Override
    public void seckill(User user, GoodsVO goods) {
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            orderService.createOder(user, goods);
        } else {
            setGoodsOver(goods.getId());
        }
    }

    @Override
    public long getSeckillResult(long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public void setGoodsOver(long goodsId) {
        redisService.set(SeckillKey.isGoodOver, "" + goodsId, true);
    }

    @Override
    public boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodOver, "" + goodsId);
    }

    @Override
    public String createSeckillPath(User user, long goodsId) {
        if (null == user || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(SeckillKey.getSeckillPath, "" + user.getId() + "_" + goodsId, str);
        return str;
    }

    /**
     * 接口路径检查
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, long goodsId, String path) {
        if (null == user || null == path) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        //设置宽度与高度
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        // set the background color
        graphics.setColor(new Color(0xDCDCDC));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.black);
        graphics.drawRect(0, 0, width - 1, height - 1);
        Random rand = null;
        try {
            rand = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            log.error("验证码random异常" + e.getMessage());
        }
        //生成50个干扰的点
        for (int i = 0; i < 50; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            graphics.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rand);
        graphics.setColor(new Color(0, 100, 0));
        graphics.setFont(new Font("Candara", Font.BOLD, 24));
        graphics.drawString(verifyCode, 8, 24);
        graphics.dispose();
        //把验证码结果存到redis中
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId, calc(verifyCode));
        //输出图片
        return bufferedImage;
    }

    @Override
    public void reset(List<GoodsVO> goodsVOList) {
        log.info("reset mysql");
        goodsService.resetStock(goodsVOList);
        orderService.deleteOrders();
    }

    @Override
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        //在redis中获取验证码结果值
        Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId, Integer.class);
        //与用户输入的验证码结果对比
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        //通过则删除验证码缓存
        redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}
