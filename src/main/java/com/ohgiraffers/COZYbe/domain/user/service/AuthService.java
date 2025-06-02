package com.ohgiraffers.COZYbe.domain.user.service;

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

    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;

    // TODO : 로그인
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        // TODO : 비밀번호 일치 하지 않다면 실행됨
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String token = jwtTokenProvider.createToken(user.getUserId());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
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
