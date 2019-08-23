package com.lin.seckill.common.enums;


public enum LoginEnum {
    SUCCESS(0, "登录成功"),
    ERROR(1, "登录失败"),
    TIMEOUT(3, "登录超时"),
    SESSION_ERROR(500210, "Session不存在或者已经失效"),
    PASSWORD_EMPTY(500211, "登录密码不能为空"),
    MOBILE_EMPTY(500212, "手机号不能为空"),
    MOBILE_ERROR(500213, "手机号格式错误"),
    MOBILE_NOT_EXIST(500214, "手机号不存在"),
    PASSWORD_ERROR(500215, "密码错误");

    Integer status;
    String message;

    LoginEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static LoginEnum getIndexOfStatus(Integer index) {
        for (LoginEnum loginEnum : values()) {
            if (loginEnum.getStatus() == index) {
                return loginEnum;
            }
        }
        return null;
    }
}
