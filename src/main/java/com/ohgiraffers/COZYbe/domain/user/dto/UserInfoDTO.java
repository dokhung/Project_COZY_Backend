package com.ohgiraffers.COZYbe.domain.user.dto;

public record UserInfoDTO(
        String email,
        String nickname,
        String profileImageUrl,
        String statusMessage
) {
}
