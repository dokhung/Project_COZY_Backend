package com.ohgiraffers.COZYbe.domain.task.dto;

public record TaskChecklistDTO(Long checklistId, String content, boolean completed, Long position) {
}
