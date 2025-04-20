package com.intern.jwtauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @Schema(description = "사용자 ID")
    private String username;
    @Schema(description = "사용자 비밀번호")
    private String password;
    @Schema(description = "사용자 닉네임")
    private String nickname;


}
