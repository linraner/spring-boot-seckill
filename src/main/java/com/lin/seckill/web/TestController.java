package com.lin.seckill.web;


import com.alibaba.fastjson.JSONObject;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.domain.User;
import com.lin.seckill.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class TestController {
    @Autowired
    private RedisService redisService;

    @RequestMapping("/test")
    @ResponseBody
    public Result<String> test(@RequestParam("b") String b, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionB = session.getAttribute("b");
        User user = new User();
        user.setId(100001L);
        user.setNickname("李白");
        user.setPassword("1123123");
        if (sessionB == null) {
            System.out.println("不存在session，设置browser=" + b);
            session.setAttribute("b", b);
            session.setAttribute("user", RedisService.beanToString(user));
        } else {
            System.out.println("存在session，browser=" + b);
            System.out.println("session info " + session.toString());
            User user1 = RedisService.stringToBean((String) session.getAttribute("user"), User.class);
            System.out.println("user info " + JSONObject.toJSONString(user1));
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            System.out.println("client cookie---------------");
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }

        return Result.success("ok");
    }

    @RequestMapping("/session")
    @ResponseBody
    public String index(HttpSession session, HttpServletRequest request) {
        HttpSession session1 = request.getSession();

        System.out.println("session: " + session);
        System.out.println("session1: " + session1);


        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            System.out.println("client cookie---------------");
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }
        return "ok";

    }

    @RequestMapping("/session2")
    @ResponseBody
    public String index2(@RequestParam("_s") String b, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            System.out.println("session is null");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            System.out.println("client cookie---------------");
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }
        return "ok";

    }

}
