package com.ohgiraffers.COZYbe.domain.teams.application.service;


import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    List<TeamNameDTO> entityToDtoList(List<Team> teams);

    @Mapping(source = "leader.nickname", target = "leaderName")
    TeamDetailDTO entityToDetail(Team team);

    List<TeamNameDTO> teamListToDto(List<Team> teamList);
}
