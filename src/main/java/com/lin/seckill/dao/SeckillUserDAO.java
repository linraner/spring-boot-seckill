package com.lin.seckill.dao;

import com.lin.seckill.model.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
public interface SeckillUserDAO {

    @Select("select * from miaosha_user where id = #{id}")
    SeckillUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(SeckillUser toBeUpdate);
}
