package com.intern.jwtauthservice.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private final String username;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private final String password;
}

