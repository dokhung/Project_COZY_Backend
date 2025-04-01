package com.ohgiraffers.COZYbe.domain.teams.application.service;


import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamListDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TeamMapper {


    TeamListDTO entityToNames(List<Team> teams);
}
