package com.intern.jwtauthservice.controller;

import com.intern.jwtauthservice.dto.*;
import com.intern.jwtauthservice.security.JwtUtil;
import com.intern.jwtauthservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(
                            schema = @Schema(implementation = SignupResponse.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "중복된 사용자" , content = @Content(examples = @ExampleObject(
                            value = "{ \"error\": { \"code\": \"USER_ALREADY_EXISTS\", \"message\": \"이미 가입된 사용자입니다.\"}}"
                    )))
            }
    )
    public ResponseEntity<?> signup(@RequestBody SignupRequest request){
        try{
            User user = userService.signup(request.getUsername(), request.getPassword(), request.getNickname());
            SignupResponse response = new SignupResponse(
                user.getUsername(),
                user.getNickname(),
                user.getRoles().stream().toList()
            );
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                "error", Map.of("code", e.getMessage(), "message", "이미 가입된 사용자 입니다.")
            ));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인",
    description = "아이디와 비밀번호를 입력 후 로그인이 완료되면 JWT토큰을 반환" ,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(examples = @ExampleObject(value = "{\"token\": \"토큰\"}"))),
                    @ApiResponse(responseCode = "400", description = "로그인 실패", content = @Content(examples = @ExampleObject(value = "{\n" +
                            "  \"error\": {\n" +
                            "    \"code\": \"INVALID_CREDENTIALS\",\n" +
                            "    \"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"\n" +
                            "  }\n" +
                            "}")))
            }
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User user = userService.login(req.getUsername(), req.getPassword());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", Map.of("code", e.getMessage(), "message", "아이디 또는 비밀번호가 올바르지 않습니다.")));
        }
    }

    @PatchMapping("/admin/users/{userId}/roles")
    @Operation(
            summary = "관리자 권한 부여",
            description = "User 권한에서 Admin 권한으로 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "권한 변경 성공",
                            content = @Content(schema = @Schema(implementation = SignupResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "만료되었거나 유효하지 않은 토큰입니다.",
                            content = @Content(
                                    examples = @ExampleObject("{\n" +
                                            "  \"error\": {\n" +
                                            "    \"code\": \"INVALID_TOKEN\",\n" +
                                            "    \"message\": \"유효하지 않은 인증 토큰입니다.\"\n" +
                                            "  }\n" +
                                            "}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 부족(접근 제한)",
                            content = @Content(
                                    examples = @ExampleObject("{\n" +
                                            "  \"error\": {\n" +
                                            "    \"code\": \"ACCESS_DENIED\",\n" +
                                            "    \"message\": \"관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.\"\n" +
                                            "  }\n" +
                                            "}")
                            )
                    )
            }
    )
    public ResponseEntity<?> grantAdmin(@PathVariable Long userId) {
        try {
            GrantAdminResponse grantAdminResponse = userService.grantAdmin(userId);
            return ResponseEntity.ok(grantAdminResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", Map.of("code", "ACCESS_DENIED", "message", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.")));
        }
    }
}
