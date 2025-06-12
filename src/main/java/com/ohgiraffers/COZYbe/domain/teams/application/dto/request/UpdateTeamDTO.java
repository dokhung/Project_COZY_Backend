package com.ohgiraffers.COZYbe.domain.teams.application.dto.request;

public record UpdateTeamDTO(
        String teamId,
        String teamName,
        String description
) {
}
