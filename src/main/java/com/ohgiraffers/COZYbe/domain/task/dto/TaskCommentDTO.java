package com.ohgiraffers.COZYbe.domain.task.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCommentDTO(Long commentId, Long parentCommentId, UUID authorId,
                             String authorNickname, String content, LocalDateTime createdAt) {
}
