package com.lin.seckill.common.execption;


import com.lin.seckill.common.result.CodeMessage;
import com.lin.seckill.common.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(Exception e) {
        if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            return Result.error(globalException.getCodeMessage());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMessage.BIND_ERROR.fillArgs(msg));
        } else {
            return Result.error(CodeMessage.SERVER_ERROR);
        }
    }
}
