package com.ohgiraffers.COZYbe.domain.teams.application.service;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.teams.domain.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository repository;
    private final TeamMapper mapper;

    public List<TeamNameDTO> getAllList() {
        List<Team> teams = repository.findAll();
        return mapper.entityToDtoList(teams);
    }

    public TeamDetailDTO createTeam() {
        return null;
    }
}
