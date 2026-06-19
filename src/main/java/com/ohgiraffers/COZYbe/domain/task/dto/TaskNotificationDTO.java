package com.ohgiraffers.COZYbe.domain.task.dto;

import java.time.LocalDateTime;

public record TaskNotificationDTO(Long notificationId, Long taskId, String type,
                                  String message, boolean read, LocalDateTime createdAt) {
}
