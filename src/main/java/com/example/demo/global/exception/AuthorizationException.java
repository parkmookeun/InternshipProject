package com.example.demo.global.exception;

public class AuthorizationException extends RuntimeException{
    public AuthorizationException(){
        super("권리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.");
    }
    public AuthorizationException(String message){
        super(message);
    }
}
