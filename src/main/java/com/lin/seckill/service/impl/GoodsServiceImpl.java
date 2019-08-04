package com.lin.seckill.service.impl;

import com.lin.seckill.dao.GoodsDAO;
import com.lin.seckill.model.SeckillGoods;
import com.lin.seckill.pojo.vo.GoodsVO;
import com.lin.seckill.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements IGoodsService {


    @Autowired
    private GoodsDAO goodsDao;

    @Override
    public List<GoodsVO> listGoodVO() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public GoodsVO getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVO goods) {
        SeckillGoods goods1 = new SeckillGoods();
        goods1.setGoodsId(goods.getId());
        return goodsDao.reduceStock(goods1) > 0;
    }

    @Override
    public void resetStock(List<GoodsVO> goodsList) {
        for (GoodsVO goods : goodsList) {
            SeckillGoods g = new SeckillGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
