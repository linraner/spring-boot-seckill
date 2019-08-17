package com.lin.seckill.dao;

import com.lin.seckill.domain.OrderInformation;
import com.lin.seckill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrderDAO {

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    void insert(OrderInformation orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id, status)values(#{userId}, #{goodsId}, #{orderId}, #{status})")
    void insertMiaoshaOrder(SeckillOrder miaoshaOrder);

    @Select("select * from miaosha_order")
    List<SeckillOrder> getSeckillOrderList();

    @Select("select * from order_info where id = #{orderId}")
    OrderInformation getOrderById(@Param("orderId") long orderId);

    @Delete("delete from order_info")
    void deleteOrders();

    @Delete("delete from miaosha_order")
    void deleteMiaoshaOrders();

    @Update(("update order_info set status=1, pay_date=#{payDate} where id = #{orderId}"))
    void updatePayOrderInformation(@Param("orderId") long orderId, @Param("payDate") Date payDate);

    void deleteSeckillOrderById();
}
