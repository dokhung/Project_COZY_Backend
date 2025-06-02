package com.ohgiraffers.COZYbe.domain.user.controller;

import com.ohgiraffers.COZYbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.COZYbe.domain.user.service.AuthService;
import com.ohgiraffers.COZYbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("🔍 로그인 요청: " + loginDTO.getEmail());

        try {
            Map<String, Object> loginResponse = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
            String token = (String) loginResponse.get("token");

            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "토큰 생성 실패"));
            }

            System.out.println("✅ 로그인 성공 - 반환 토큰: " + token);
            return ResponseEntity.ok().body(Map.of("token", token, "user", loginResponse.get("user")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 필요합니다."));
        }

        try {
            String jwtToken = token.substring(7);
//            String email = userService.getUserIdFromToken(jwtToken);
            authService.invalidateToken(jwtToken);
            return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "로그아웃 중 오류 발생: " + e.getMessage()));
        }
    }


}
