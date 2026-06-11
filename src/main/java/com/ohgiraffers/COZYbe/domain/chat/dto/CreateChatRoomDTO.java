package com.ohgiraffers.COZYbe.domain.chat.dto;

import java.util.List;

public record CreateChatRoomDTO(
        String name,
        boolean direct,
        List<String> memberIds
) {
}
