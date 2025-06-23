package com.ohgiraffers.COZYbe.domain.user.service;

import com.ohgiraffers.COZYbe.domain.user.dto.AuthTokenDTO;
import com.ohgiraffers.COZYbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthTokenDTO login(LoginDTO loginDTO) {
        UUID userId = userService.verifyUser(loginDTO);
        System.out.println("userId :: " + userId);
        return new AuthTokenDTO(
                jwtTokenProvider.createToken(userId),
                jwtTokenProvider.getValidTime()
        );
    }



    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.decodeUserIdFromJwt(token);
    }
}
