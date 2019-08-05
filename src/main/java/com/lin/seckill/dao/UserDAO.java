package com.lin.seckill.dao;

import com.lin.seckill.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDAO {

    @Select("select * from miaosha_user where id = #{id}")
    User getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(User toBeUpdate);
}
