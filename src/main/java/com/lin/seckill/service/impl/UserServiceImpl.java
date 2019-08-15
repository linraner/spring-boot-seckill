package com.lin.seckill.service.impl;

import com.lin.seckill.common.execption.GlobalException;
import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.util.MD5Util;
import com.lin.seckill.common.util.UUIDUtil;
import com.lin.seckill.dao.UserDAO;
import com.lin.seckill.domain.User;
import com.lin.seckill.vo.LoginVO;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.redis.UserKey;
import com.lin.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements IUserService {
    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserDAO userDAO;

    @Override
    public User getById(long id) {
        // 从缓冲中读取
        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }

        user = userDAO.getById(id);
        if (user != null) {
            redisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    @Override
    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null) {
            throw new GlobalException(CodeMessage.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String formPassword = loginVO.getPassword();

        User user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMessage.MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPassword, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMessage.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    @Override
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmptyOrWhitespace(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        // 延长有效期
        if (null == user) {
            addCookie(response, token, user);
        }
        return user;
    }


    private void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
