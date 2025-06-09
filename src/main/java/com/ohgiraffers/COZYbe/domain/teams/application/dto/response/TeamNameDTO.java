package com.ohgiraffers.COZYbe.domain.teams.application.dto.response;

import java.util.UUID;

public record TeamNameDTO(
        UUID teamId,
        String teamName
) {
}
