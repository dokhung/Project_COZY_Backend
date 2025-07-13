package com.ohgiraffers.COZYbe.domain.user.controller;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.user.dto.AuthTokenDTO;
import com.ohgiraffers.COZYbe.domain.user.dto.LoginDTO;
import com.ohgiraffers.COZYbe.domain.user.service.AuthService;
import com.ohgiraffers.COZYbe.domain.user.service.BlocklistService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final BlocklistService blocklistService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("loginDTO : " + loginDTO);
        AuthTokenDTO authTokenDTO = authService.login(loginDTO);
        return ResponseEntity.ok().body(authTokenDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        String jti = jwt.getId();

        long ttl  = jwt.getExpiresAt().toEpochMilli() - System.currentTimeMillis();
        blocklistService.store(jti, ttl);
        System.out.println("ttl :: " + ttl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "refreshToken",
            required = false) String refreshToken
    ){
        if(refreshToken == null || refreshToken.isEmpty()){
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        }

        String userId = jwtTokenProvider.decodeUserIdFromJwt(refreshToken);
        String newAccessToken = jwtTokenProvider.createToken(UUID.fromString(userId));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }


}
