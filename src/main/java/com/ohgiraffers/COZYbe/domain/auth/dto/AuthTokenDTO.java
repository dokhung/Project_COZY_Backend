package com.ohgiraffers.COZYbe.domain.auth.dto;

public record AuthTokenDTO(
        String accessToken,
//        Long accessTokenValidTime
        String refreshToken
) {
}
