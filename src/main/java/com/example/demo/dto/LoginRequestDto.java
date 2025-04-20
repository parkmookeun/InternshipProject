package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @Schema(description = "사용자 이름", example = "JIN HO")
    private String username;
    @Schema(description = "사용자 비밀번호", example = "12341234")
    private String password;


}
