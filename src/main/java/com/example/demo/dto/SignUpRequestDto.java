package com.example.demo.dto;

import com.example.demo.global.constants.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpRequestDto {


    @Schema(description = "사용자 이름", example = "JIN HO")
    private String username;
    @Schema(description = "사용자 비밀번호", example = "12341234")
    private String password;
    @Schema(description = "별명", example = "Mentos")
    private String nickname;

    private Role role;

    public Role getRole() {
        return role == null ? Role.USER : role;
    }
}
