package com.example.demo.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final ErrorCode error;

    public ErrorResponse(ErrorCode error){
        this.error = error;
    }
}
