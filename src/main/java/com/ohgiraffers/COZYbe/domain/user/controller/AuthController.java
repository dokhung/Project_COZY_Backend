package com.ohgiraffers.COZYbe.domain.user.controller;

import com.ohgiraffers.COZYbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.COZYbe.domain.user.service.AuthService;
import com.ohgiraffers.COZYbe.domain.user.service.BlocklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private BlocklistService blocklistService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("🔍 로그인 요청: " + loginDTO.getEmail());

        return ResponseEntity.ok().body(authService.login(loginDTO));

    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 필요합니다."));
//        }
//
//        try {
//            String jwtToken = token.substring(7);
//            authService.invalidateToken(jwtToken);
//            return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("error", "로그아웃 중 오류 발생: " + e.getMessage()));
//        }
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        String jti = jwt.getId();

        long ttl  = jwt.getExpiresAt().toEpochMilli() - System.currentTimeMillis();
        blocklistService.store(jti, ttl);
        return ResponseEntity.ok().build();
    }


}
