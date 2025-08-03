package com.ohgiraffers.COZYbe.domain.user.dto;

public record AuthTokenDTO(
        String accessToken,
//        Long accessTokenValidTime
        String refreshToken
) {
}
