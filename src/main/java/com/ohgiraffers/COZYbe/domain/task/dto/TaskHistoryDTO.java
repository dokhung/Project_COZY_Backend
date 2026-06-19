package com.ohgiraffers.COZYbe.domain.task.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskHistoryDTO(Long historyId, UUID actorId, String actorNickname,
                             String action, String detail, LocalDateTime createdAt) {
}
