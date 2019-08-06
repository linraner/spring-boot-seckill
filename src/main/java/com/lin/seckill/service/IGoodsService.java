package com.lin.seckill.service;

import com.lin.seckill.pojo.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IGoodsService {

    /**
     *
     * @return
     */
    List<GoodsVO> listGoodsVO();

    /**
     *
     * @param goodsId
     * @return
     */
    GoodsVO getGoodsVoByGoodsId(long goodsId);

    /**
     *
     * @param goods
     * @return
     */
    boolean reduceStock(GoodsVO goods);

    /**
     *
     * @param goodsList
     */
    void resetStock(List<GoodsVO> goodsList);
}
