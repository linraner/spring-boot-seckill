package com.lin.seckill.common.execption;

import com.lin.seckill.common.result.CodeMessage;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class GlobalException extends RuntimeException implements Serializable {
    private final transient CodeMessage codeMessage;


    public GlobalException(CodeMessage codeMessage) {
        super(codeMessage.toString());
        this.codeMessage = codeMessage;
    }
}
