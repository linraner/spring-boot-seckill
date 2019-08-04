package com.lin.seckill.common.result;

import lombok.Data;

@Data
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    /**
     * 成功时候的调用
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    /**
     * 失败时候的调用
     */
    public static <T> Result<T> error(CodeMessage codeMsg) {
        return new Result<>(codeMsg);
    }

    private Result(T data) {
        this.data = data;
    }

    private Result(CodeMessage codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }
}
