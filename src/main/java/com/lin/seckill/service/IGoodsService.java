package com.lin.seckill.service;

import com.lin.seckill.pojo.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IGoodsService {

    List<GoodsVO> listGoodVO();

    GoodsVO getGoodsVoByGoodsId(long goodsId);

    boolean reduceStock(GoodsVO goods);

    void resetStock(List<GoodsVO> goodsList);
}
