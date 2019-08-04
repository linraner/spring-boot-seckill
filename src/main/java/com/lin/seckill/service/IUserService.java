package com.lin.seckill.service;


import com.lin.seckill.model.SeckillUser;
import com.lin.seckill.pojo.vo.LoginVO;

import javax.servlet.http.HttpServletResponse;

public interface IUserService {


    /**
     * 根据手机号获取用户
     * @param id
     * @return
     */
    SeckillUser getById(long id);


    /**
     * 登录生成token
     * @param response
     * @param loginVO
     * @return
     */
    String login(HttpServletResponse response, LoginVO loginVO);


    /**
     * 根据token获取用户信息
     * @param response
     * @param token
     * @return
     */
    SeckillUser getByToken(HttpServletResponse response, String token);

}
