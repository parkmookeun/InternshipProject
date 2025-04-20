package com.example.demo.dto;

import com.example.demo.entity.User;
import com.example.demo.global.constants.Role;
import lombok.Getter;

@Getter
public class UpdateRoleResponseDto {
    private String username;
    private String nickname;
    private Role role;

    public UpdateRoleResponseDto(User user){
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
