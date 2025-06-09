package com.ohgiraffers.COZYbe.domain.teams.application.dto.response;


import java.util.UUID;

public record TeamDetailDTO(
        UUID teamId,
        String teamName,
        String description
) {
}
