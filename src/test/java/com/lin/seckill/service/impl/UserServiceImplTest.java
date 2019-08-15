package com.lin.seckill.service.impl;

import com.lin.seckill.common.execption.GlobalException;
import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.util.MD5Util;
import com.lin.seckill.common.util.UUIDUtil;
import com.lin.seckill.domain.User;
import com.lin.seckill.vo.LoginVO;
import com.lin.seckill.redis.RedisService;
import com.lin.seckill.redis.UserKey;
import com.lin.seckill.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Test
    public void getById() {

    }

    @Test
    public void login() {



    }

    @Test
    public void getByToken() {
        LoginVO loginVO = new LoginVO("18912341234","d3b1294a61a07da9b49b6e22b2cbd7f9");
        if (loginVO == null) {
            throw new GlobalException(CodeMessage.SERVER_ERROR);
        }
        // 生成token
        String mobile = loginVO.getMobile();
        String formPassword = loginVO.getPassword();

        User user = userService.getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMessage.MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPassword, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMessage.PASSWORD_ERROR);
        }


        // 生成token
        String token = UUIDUtil.uuid();
        redisService.set(UserKey.token, token, user);
        User user1 = redisService.get(UserKey.token, token, User.class);
        if (user1 != null) {
            System.out.println("UserKey: " + UserKey.token);
            System.out.println("UserKey-value: " + token);

            System.out.println(user1);
        }


    }

}