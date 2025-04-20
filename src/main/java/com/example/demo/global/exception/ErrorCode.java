package com.example.demo.global.exception;

import lombok.Getter;

@Getter
public class ErrorCode {

    private final String code;
    private final String message;

    public ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }
}
