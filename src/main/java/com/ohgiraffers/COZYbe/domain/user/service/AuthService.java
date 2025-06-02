package com.ohgiraffers.COZYbe.domain.user.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.user.dto.AuthTokenDTO;
import com.ohgiraffers.COZYbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthService {

    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;


    public AuthTokenDTO login(LoginDTO dto) {
        User user = userService.findUserByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        String token = jwtTokenProvider.createToken(user.getUserId());
        return new AuthTokenDTO(
                token,
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
