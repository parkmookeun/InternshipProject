package com.example.demo.global.exception;


import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    public CustomException(){
        super("이미 가입된 사용자입니다.");
    }
    public CustomException(String message){
        super(message);
    }
}

