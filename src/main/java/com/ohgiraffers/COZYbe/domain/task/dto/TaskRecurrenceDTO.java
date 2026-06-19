package com.ohgiraffers.COZYbe.domain.task.dto;

import java.time.LocalDate;

public record TaskRecurrenceDTO(String type, Integer interval, LocalDate until) {
}
