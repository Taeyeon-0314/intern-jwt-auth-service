package com.intern.jwtauthservice.service;

import com.intern.jwtauthservice.dto.GrantAdminResponse;
import com.intern.jwtauthservice.dto.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Map<String, User> userMap = new HashMap<>();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Long userIdCounter = 1L;

    public User signup(String username, String password, String nickname){
        if(userMap.containsKey(username)) throw new RuntimeException(
                "User_ALREADY_EXISTS"
        );
        User user = User.builder()
                .id(userIdCounter++)
                .username(username)
                .password(encoder.encode(password))
                .nickname(nickname)
                .roles(new HashSet<>(List.of("User")))
                .build();
        userMap.put(username,user);
        return user;
    }

    public User login(String username, String password){
        User user = userMap.get(username);
        if ( user == null || !encoder.matches(password, user.getPassword())){
            throw new RuntimeException("INVALID_CREDENTIALS");
        }
        return user;
    }

    public GrantAdminResponse grantAdmin(Long userId){
        return userMap.values().stream()
                .filter(user -> Objects.equals(user.getId(), userId))
                .findFirst()
                .map(user -> {
                    user.getRoles().add("ADMIN");
                    return new GrantAdminResponse(user.getUsername(), user.getNickname(), user.getRoles());
                })
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    }
}
