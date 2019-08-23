package com.lin.seckill.common.access;

import com.alibaba.fastjson.JSON;
import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.domain.User;
import com.lin.seckill.redis.AccessKey;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


@Service
public class AccessInterceptor2 extends HandlerInterceptorAdapter {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HttpSession session = request.getSession();
            User user = getUser(session);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取注解对象
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMessage.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }

            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey, key, Integer.class);
            if (count == null) {
                redisService.set(accessKey, key, 1);
            } else if (count < maxCount) {
                redisService.incr(accessKey, key);
            } else {
                render(response, CodeMessage.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMessage codeMsg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

    private User getUser(HttpSession session) {
        return RedisService.stringToBean((String) session.getAttribute("user"), User.class);
    }

}
