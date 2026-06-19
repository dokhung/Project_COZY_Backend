package com.ohgiraffers.COZYbe.domain.task.dto;

import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TaskDetailDTO(
        Long taskId,
        UUID projectId,
        UUID userId,
        String title,
        String assigneeNickname,
        TaskStatus status,
        TaskPriority priority,
        String taskText,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
