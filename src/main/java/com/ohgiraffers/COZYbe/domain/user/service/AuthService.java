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

        String accessToken = jwtTokenProvider.createToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        return new AuthTokenDTO(
                accessToken,
//                jwtTokenProvider.getValidTime()
                refreshToken
        );
    }



    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.decodeUserIdFromJwt(token);
    }
}
