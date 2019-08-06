package com.lin.seckill.service;

import com.lin.seckill.pojo.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IGoodsService {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVO> listGoodsVO();

    /**
     * 根据goodsId获取商品
     * @param goodsId
     * @return
     */
    GoodsVO getGoodsVoByGoodsId(long goodsId);

    /**
     * 减库存
     * @param goods
     * @return
     */
    boolean reduceStock(GoodsVO goods);

    /**
     * 重置库存接口
     * @param goodsList
     */
    void resetStock(List<GoodsVO> goodsList);
}
