package com.ohgiraffers.collaboprojectbe.domain.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.SignUpDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.entity.User;
import com.ohgiraffers.collaboprojectbe.domain.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public ResponseEntity<?> signup(
            @RequestPart("signUpDTO") String signUpDTOJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SignUpDTO signUpDTO = objectMapper.readValue(signUpDTOJson, SignUpDTO.class);
            // 비밀번호와 확인 비밀번호의 일치 유무
            if (!Objects.equals(signUpDTO.getConfirmPassword(), signUpDTO.getPassword())){
                System.out.println("비밀번호와 확인 비밀번호가 같음");
            }
            User user = authService.register(signUpDTO, profileImage);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류 발생: " + e.getMessage());
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("loginDTO :: " + loginDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.status(403).body("이미 로그인된 상태입니다.");
        }

        try {
            Map<String, Object> loginResponse = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.get("token"))
                    .body(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 현재 로그인된 사용자 정보 확인 엔드포인트
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Accessing current-user endpoint");

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String userId = authentication.getName();
            System.out.println("Authenticated user ID in current-user API: " + userId);
            try {
                User user = authService.getUserInfo(userId);
                return ResponseEntity.ok(user);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(404).body("User not found in database.");
            }
        } else {
            return ResponseEntity.status(401).body("No user is currently logged in.");
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        return ResponseEntity.ok().body(Map.of("available", isAvailable));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String userEmail = authentication.getName();
        String inputPassword = request.get("password");

        try {
            boolean isValid = authService.verifyPassword(userEmail, inputPassword);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", "비밀번호가 일치하지 않습니다."));
        }
    }

    @PostMapping("/update-info")
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String userEmail = authentication.getName();
        String newNickname = request.get("nickname");
        String newStatusMessage = request.get("statusMessage");

        try {
            User updatedUser = authService.updateUserInfo(userEmail, newNickname, newStatusMessage);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }



}
