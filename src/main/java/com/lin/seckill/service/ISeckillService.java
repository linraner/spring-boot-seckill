package com.lin.seckill.service;

import com.lin.seckill.domain.User;
import com.lin.seckill.vo.GoodsVO;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ISeckillService {

    /**
     * 秒杀 减库存 下订单 写入秒杀订单
     *
     * @param user
     * @param goods
     */
    @Transactional
    void seckill(User user, GoodsVO goods);

    /**
     * 查询秒杀状态
     *
     * @param userId
     * @param goodsId
     * @return
     */
    long getSeckillResult(long userId, long goodsId);


    /**
     * @param goodsId
     */
    void setGoodsOver(long goodsId);

    /**
     * @param goodsId
     * @return
     */
    boolean getGoodsOver(long goodsId);

    /**
     * 生成秒杀路径
     *
     * @param user
     * @param goodsId
     * @return
     */
    String createSeckillPath(User user, long goodsId);

    /**
     * 检查秒杀路径
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, long goodsId, String path);


    /**
     * 生成秒杀验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    BufferedImage createVerifyCode(User user, long goodsId);

    /**
     * 检查验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    boolean checkVerifyCode(User user, long goodsId, int verifyCode);

    /**
     * 初始化
     * @param goodsVOList
     */
    void reset(List<GoodsVO> goodsVOList);
}
