package com.ohgiraffers.COZYbe.domain.task.dto;

import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskDTO(
        UUID projectId,
        UUID assigneeId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        String taskText,
        LocalDate startDate,
        LocalDate dueDate
) {
}
