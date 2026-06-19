package com.ohgiraffers.COZYbe.domain.task.dto;

import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;

public record TaskBoardMoveDTO(TaskStatus status, Long position) {
}
