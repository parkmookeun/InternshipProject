package com.example.demo.global.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(){
        super("유효하지 않은 인증 토큰입니다.");
    }
    public InvalidTokenException(String message){
        super(message);
    }
}
