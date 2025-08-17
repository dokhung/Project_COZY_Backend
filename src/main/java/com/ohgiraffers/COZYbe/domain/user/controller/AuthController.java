package com.ohgiraffers.COZYbe.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.user.dto.*;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.service.AuthService;
import com.ohgiraffers.COZYbe.domain.user.service.BlocklistService;
import com.ohgiraffers.COZYbe.domain.user.service.UserService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final BlocklistService blocklistService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    //ログイン
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        AuthTokenDTO authTokenDTO = authService.login(loginDTO);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authTokenDTO.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(Map.of(
                        "accessToken", authTokenDTO.accessToken()
                ));
    }

    //ログアウト
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        String jti = jwt.getId();

        long ttl  = jwt.getExpiresAt().toEpochMilli() - System.currentTimeMillis();
        blocklistService.store(jti, ttl);
        System.out.println("ttl :: " + ttl);
        return ResponseEntity.ok().build();
    }

    //レプレシトークン
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ){
        if (refreshToken == null || refreshToken.isEmpty()){
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        }

        String userId = jwtTokenProvider.decodeUserIdFromJwt(refreshToken);
        System.out.println("userId" + userId);
        String newAccessToken = jwtTokenProvider.createToken(UUID.fromString(userId));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 필요합니다."));
        }

        try {
            String jwt = token.substring(7);
            String userId = authService.getUserIdFromToken(jwt);
            UserInfoDTO userInfoDTO = userService.getUserInfo(userId);
            return ResponseEntity.ok(userInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "사용자 조회 실패: " + e.getMessage()));
        }
    }

    // 🔹 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 없습니다."));
        }

        System.out.println("🔍 받은 인증 토큰: " + token);


        String userId;
        try {
            userId = authService.getUserIdFromToken(token.substring(7)); // "Bearer " 제거 후 이메일 추출
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "유효하지 않은 토큰입니다."));
        }

        String inputPassword = request.get("password");

        if (inputPassword == null) {
            System.out.println("❌ 비밀번호가 전달되지 않음");
            return ResponseEntity.status(400).body(Map.of("error", "비밀번호가 필요합니다."));
        }

        try {
            boolean isValid = userService.verifyPassword(userId, inputPassword);

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
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 없습니다."));
        }

        try {
            String userId = authService.getUserIdFromToken(token.substring(7));

            UserUpdateDTO dto = new UserUpdateDTO(nickname, statusMessage);
            User updatedUser = userService.updateUserInfo(userId, dto, profileImage);

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "정보 수정 실패: " + e.getMessage()));
        }
    }


    // 회원가입 (프로필 이미지 포함)
    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public ResponseEntity<?> signup(
            @RequestPart("signUpDTO") String signUpDTOJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SignUpDTO signUpDTO = objectMapper.readValue(signUpDTOJson, SignUpDTO.class);

            if (!Objects.equals(signUpDTO.getConfirmPassword(), signUpDTO.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }

            User user = userService.register(signUpDTO, profileImage);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "회원가입 중 오류 발생: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpDTO signUpDTO){
        UserInfoDTO userInfoDTO = userService.registerDefault(signUpDTO);
        return ResponseEntity.ok(userInfoDTO);
    }


    @GetMapping("/check-current")
    public ResponseEntity<?> checkCurrentUser(@AuthenticationPrincipal Jwt jwt){
        String sub = jwt.getSubject();
        UserInfoDTO userInfoDTO = userService.getUserInfo(sub);
        return ResponseEntity.ok(userInfoDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }



}
