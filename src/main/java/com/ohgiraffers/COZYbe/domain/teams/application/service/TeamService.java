package com.ohgiraffers.COZYbe.domain.teams.application.service;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamListDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.teams.domain.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamService {

    private TeamRepository repository;
    private TeamMapper mapper;

    public TeamListDTO getAllList() {
        List<Team> teams = repository.findAll();
        return mapper.entityToNames(teams);
    }

    public TeamNameDTO createTeam() {
        return null;
    }
}
