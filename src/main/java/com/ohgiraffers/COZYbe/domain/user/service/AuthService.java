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

    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;


    public AuthTokenDTO login(LoginDTO loginDTO) {
        UUID userId = userService.verifyUser(loginDTO);
        return new AuthTokenDTO(
                jwtTokenProvider.createToken(userId),
                jwtTokenProvider.getValidTime()
        );
    }



    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.decodeUserIdFromJwt(token);
    }


    private Set<String> invalidatedTokens = new HashSet<>();

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
        System.out.println("🚀 [토큰 무효화] 저장된 무효화된 토큰 개수: " + invalidatedTokens.size());
    }

    public boolean isTokenValid(String token) {
        boolean isValid = !invalidatedTokens.contains(token);
        return isValid;
    }


}
