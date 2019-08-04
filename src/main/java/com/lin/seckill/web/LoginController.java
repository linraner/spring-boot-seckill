package com.lin.seckill.web;

import com.lin.seckill.common.result.Result;
import com.lin.seckill.pojo.vo.LoginVO;
import com.lin.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private IUserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @PostMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO loginVO) {
        String token = userService.login(response, loginVO);

        return Result.success(token);

    }
}
