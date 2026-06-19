package com.ohgiraffers.COZYbe.domain.task.dto;

import java.time.LocalDateTime;

public record TaskAttachmentDTO(Long attachmentId, String originalName, String url,
                                String contentType, long size, LocalDateTime createdAt) {
}
