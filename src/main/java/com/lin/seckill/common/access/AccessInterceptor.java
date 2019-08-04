package com.lin.seckill.common.access;

import com.lin.seckill.model.SeckillUser;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IUserService;
import com.lin.seckill.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;


    /**
     * 获取用户
     * @param request
     * @param response
     * @return
     */
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(UserServiceImpl.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request);
        if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);

    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(UserServiceImpl.COOKI_NAME_TOKEN)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
