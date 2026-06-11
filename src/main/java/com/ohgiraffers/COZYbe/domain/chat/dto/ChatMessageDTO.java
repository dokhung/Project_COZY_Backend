package com.ohgiraffers.COZYbe.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageDTO(
        String messageId,
        String senderId,
        String senderNickname,
        String senderProfileImage,
        String currentScheduleTitle,
        LocalDateTime currentScheduleStartAt,
        LocalDateTime currentScheduleEndAt,
        String content,
        LocalDateTime createdAt
) {
}
