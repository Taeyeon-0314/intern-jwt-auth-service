package com.intern.jwtauthservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intern.jwtauthservice.controller.AuthController;
import com.intern.jwtauthservice.dto.GrantAdminResponse;
import com.intern.jwtauthservice.dto.LoginRequest;
import com.intern.jwtauthservice.dto.SignupRequest;
import com.intern.jwtauthservice.dto.User;
import com.intern.jwtauthservice.security.JwtUtil;
import com.intern.jwtauthservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(AuthController.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@TestPropertySource(properties = {
        "jwt.secret=O4dgjkF2EqT1SYUkhGQRIvjwAqGyFAHhPrTOFJvGSm8=",
        "jwt.expiration=7200000"
})
class JwtAuthServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("회원가입 성공 시 ")
    void signup_success() throws Exception {

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(1L)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(), any(), any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디 중복으로 인한 실패")
    void signup_bad() throws Exception {

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        given(userService.signup(any(), any(), any()))
                .willThrow(new RuntimeException("USER_ALREADY_EXISTS"));

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공 여부")
    void login_success() throws Exception {

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(1L)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(),any(),any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("Taeyeon", "qwerqwer123!!!");

        given(userService.login(any() ,any())).willReturn(mockUser);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @DisplayName("로그인 실패 여부")
    void login_bad() throws Exception {

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(1L)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(),any(),any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("Taeyeon1", "qwerqwer123!!!123");

        given(userService.login(any(), any()))
                .willThrow(new RuntimeException("INVALID_CREDENTIALS"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("토큰 포함 시 관리자 권한 부여 성공")
    void grantAdmin_withToken_success() throws Exception {
        Long userId = 1L;

        SignupRequest adminSignupRequest = new SignupRequest("adminUser", "qwerqwer123!!!", "adminUser");
        User adminUser = User.builder()
                .id(userId)
                .username("adminUser")
                .password("qwerqwer123!!!")
                .nickname("adminUser")
                .roles(Set.of("ADMIN"))
                .build();
        given(userService.signup(any(), any(), any())).willReturn(adminUser);
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminSignupRequest)))
                .andExpect(status().isOk());

        given(userService.login(any(), any())).willReturn(adminUser);

        LoginRequest adminLoginRequest = new LoginRequest("adminUser", "qwerqwer123!!!");
        MvcResult result = mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(2L)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(), any(), any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        GrantAdminResponse userAfter = GrantAdminResponse.builder()
                .username("Taeyeon")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("ADMIN"))
                .build();
        given(userService.grantAdmin(userId)).willReturn(userAfter);

        mockMvc.perform(patch("/admin/users/" + userId + "/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Taeyeon"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    @DisplayName("권한이 부족한 경우")
    void grantAdmin_forbidden() throws Exception {
        Long userId = 1L;

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(userId)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(),any(),any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("Taeyeon", "qwerqwer123!!!");

        given(userService.login(any() ,any())).willReturn(mockUser);

        MvcResult result = mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();


        mockMvc.perform(patch("/admin/users/" + userId + "/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.error.message").value("관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."));


    }


    @Test
    @DisplayName("유효하지 않은 토큰 사용")
    void grantAdmin_invalid() throws Exception {
        Long userId = 1L;

        SignupRequest request = new SignupRequest("Taeyeon", "qwerqwer123!!!", "LEETAEYEYEON");

        User mockUser = User.builder()
                .id(userId)
                .username("Taeyeon")
                .password("qwerqwer123!!!")
                .nickname("LEETAEYEYEON")
                .roles(Set.of("USER"))
                .build();

        given(userService.signup(any(),any(),any())).willReturn(mockUser);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());




        mockMvc.perform(patch("/admin/users/" + userId + "/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVc2VyIl0sInN1YiI6InF3ZXJxd2VyMSIsImlhdCI6MTc0NTEzNjc3MCwiZXhwIjoxNzQ1MTQzOTcwfQ.BiJjajJh-Rh_piUyyKf-M52jCYcY84DObsf_QNdEuY8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.error.message").value("유효하지 않은 인증 토큰입니다."));


    }

}
