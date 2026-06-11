package com.ohgiraffers.COZYbe.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomDTO(
        String roomId,
        String name,
        boolean direct,
        long memberCount,
        String lastMessage,
        LocalDateTime lastMessageAt
) {
}
