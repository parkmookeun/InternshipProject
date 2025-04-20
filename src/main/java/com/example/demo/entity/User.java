package com.example.demo.entity;

import com.example.demo.global.constants.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    private String username;
    private String password;
    private String nickname;
    @Setter
    private Role role;

    public User(String username, String password, String nickname, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

}
