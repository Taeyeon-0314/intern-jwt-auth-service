package com.intern.jwtauthservice.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SignupResponse {
    private final String username;
    private final String nickname;
    private final List<RoleWrapper> roles;

    public SignupResponse(String username, String nickname, List<String> roles) {
        this.username = username;
        this.nickname = nickname;
        this.roles = roles.stream()
                .map(RoleWrapper::new)
                .toList();
    }

    @Getter
    public static class RoleWrapper {
        private final String role;

        public RoleWrapper(String role) {
            this.role = role;
        }
    }
}