package com.ohgiraffers.COZYbe.domain.task.enums;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    public static TaskStatus fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return TODO;
        }
        return switch (value.trim().toUpperCase()) {
            case "BEFORE" -> TODO;
            case "DOING", "APPROVING", "MERGE_REQ" -> IN_PROGRESS;
            case "MERGED" -> DONE;
            default -> TaskStatus.valueOf(value.trim().toUpperCase());
        };
    }
}
