package com.ohgiraffers.collaboprojectbe.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.SignUpDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.UserUpdateDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.entity.User;
import com.ohgiraffers.collaboprojectbe.domain.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 🔹 회원가입 (프로필 이미지 포함)
    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public ResponseEntity<?> signup(
            @RequestPart("signUpDTO") String signUpDTOJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SignUpDTO signUpDTO = objectMapper.readValue(signUpDTOJson, SignUpDTO.class);

            // 비밀번호 검증
            if (!Objects.equals(signUpDTO.getConfirmPassword(), signUpDTO.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }

            User user = authService.register(signUpDTO, profileImage);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "회원가입 중 오류 발생: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("🔍 로그인 요청: " + loginDTO.getEmail());

        try {
            Map<String, Object> loginResponse = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
            String token = (String) loginResponse.get("token");

            if (token == null || token.isEmpty()) {
                System.out.println("❌ 토큰이 반환되지 않음!");
                return ResponseEntity.status(500).body(Map.of("error", "토큰 생성 실패"));
            }

            System.out.println("✅ 로그인 성공 - 반환 토큰: " + token);
            return ResponseEntity.ok().body(Map.of("token", token, "user", loginResponse.get("user")));
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 로그인 실패: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }



    // 🔹 현재 로그인된 사용자 정보 조회
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 필요합니다."));
        }

        try {
            String email = authService.getEmailFromToken(token.substring(7));
            User user = authService.getUserInfo(email);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }
    }


    // 🔹 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("❌ 인증 토큰 없음");
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 없습니다."));
        }

        System.out.println("🔍 받은 인증 토큰: " + token);

        // 🚀 [수정] getEmailFromToken()을 제대로 활용하도록 변경
        String userEmail;
        try {
            userEmail = authService.getEmailFromToken(token.substring(7)); // "Bearer " 제거 후 이메일 추출
            System.out.println("✅ 추출된 이메일: " + userEmail);
        } catch (Exception e) {
            System.out.println("❌ 토큰에서 이메일 추출 실패");
            return ResponseEntity.status(400).body(Map.of("error", "유효하지 않은 토큰입니다."));
        }

        // 클라이언트에서 전달된 비밀번호 가져오기
        String inputPassword = request.get("password");

        if (inputPassword == null) {
            System.out.println("❌ 비밀번호가 전달되지 않음");
            return ResponseEntity.status(400).body(Map.of("error", "비밀번호가 필요합니다."));
        }

        try {
            boolean isValid = authService.verifyPassword(userEmail, inputPassword);

            if (isValid) {
                System.out.println("✅ 비밀번호 확인 성공");
                return ResponseEntity.ok(Map.of("valid", true));
            } else {
                System.out.println("❌ 비밀번호 불일치");
                return ResponseEntity.status(400).body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/update-info", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestParam("nickname") String nickname,
            @RequestParam("statusMessage") String statusMessage,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 없습니다."));
        }

        try {
            String email = authService.getEmailFromToken(token.substring(7));

            UserUpdateDTO userUpdateDTO = new UserUpdateDTO(nickname, statusMessage);
            User updatedUser = authService.updateUserInfo(email, userUpdateDTO, profileImage);

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "정보 수정 중 오류 발생: " + e.getMessage()));
        }
    }




}
