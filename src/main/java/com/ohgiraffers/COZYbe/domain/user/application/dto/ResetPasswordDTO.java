package com.ohgiraffers.COZYbe.domain.user.application.dto;

public record ResetPasswordDTO(
        String email,
        String nickname,
        String newPassword,
        String confirmPassword
) {
}
