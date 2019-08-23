package com.lin.seckill.web;

import com.lin.seckill.common.enums.LoginEnum;
import com.lin.seckill.common.result.Result;
import com.lin.seckill.vo.LoginVO;
import com.lin.seckill.service.IUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@Api(tags = "登录接口")
public class LoginController {

    @Autowired
    private IUserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @ApiOperation(value = "登录", notes = "根据loginVO登录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "loginVO", value = "用户实体类user", required = true, dataType = "LoginVO")
    })

    @PostMapping("/do_login")
    @ResponseBody
    public Result<LoginEnum> doLogin(HttpServletRequest request, HttpServletResponse response, @Valid LoginVO loginVO) {
        String token = userService.login(request, response, loginVO);
        return Result.success(LoginEnum.SUCCESS);
    }
}
