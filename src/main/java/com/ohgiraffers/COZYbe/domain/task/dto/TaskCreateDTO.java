package com.ohgiraffers.COZYbe.domain.task.dto;

import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TaskCreateDTO(
        Long id,
        String title,
        TaskStatus status,
        TaskPriority priority,
        String taskText,
        UUID assigneeId,
        String assigneeNickname,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDateTime createdAt
) {
}
