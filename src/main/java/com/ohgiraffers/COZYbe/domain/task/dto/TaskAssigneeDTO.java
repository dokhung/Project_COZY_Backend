package com.ohgiraffers.COZYbe.domain.task.dto;

import java.util.UUID;

public record TaskAssigneeDTO(UUID userId, String nickname, String profileImageUrl) {
}
