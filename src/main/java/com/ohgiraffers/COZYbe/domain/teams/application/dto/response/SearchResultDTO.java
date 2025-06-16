package com.ohgiraffers.COZYbe.domain.teams.application.dto.response;

import java.util.List;

public record SearchResultDTO(
        List<TeamNameDTO> teamList
) {
}
