package com.intern.jwtauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GrantAdminResponse {
    private String username;
    private String nickname;
    private Set<String> roles = new HashSet<>();
}
