package com.example.demo.global.constants;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    USER,ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        try {
            return value == null ? USER : valueOf(value);
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
