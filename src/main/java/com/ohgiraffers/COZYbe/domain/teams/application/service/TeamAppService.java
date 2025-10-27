package com.ohgiraffers.COZYbe.domain.teams.application.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.domain.service.MemberDomainService;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateSubLeaderDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.SearchResultDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.teams.domain.service.TeamDomainService;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import com.ohgiraffers.COZYbe.domain.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamAppService {

    private final TeamDomainService domainService;
    private final TeamMapper mapper;

    private final MemberDomainService memberDomainService;
    private final UserDomainService userDomainService;


    public SearchResultDTO getAllList() {
        List<Team> teams = domainService.getAllTeams();
        return new SearchResultDTO(mapper.entityListToDto(teams));
    }

    @Transactional
    public TeamDetailDTO createTeam(CreateTeamDTO createTeamDTO, String userId) {
        Team newTeam = Team.builder()
                .teamName(createTeamDTO.teamName())
                .description(createTeamDTO.description())
                .leader(userDomainService.getReference(userId))
                .build();

        Team created = domainService.saveTeam(newTeam);
        log.info("팀 생성됨 : {}", created.getTeamName());
        memberDomainService.createMember(created, userDomainService.getUser(userId));
        return mapper.entityToDetail(created);
    }

    public boolean checkTeamNameExist(String teamName) {
        return domainService.isTeamNameExist(teamName);
    }

    public TeamDetailDTO getTeamDetail(String teamId, String userId) {
        verifyUser(userId);

        Team team = domainService.getTeam(teamId);
        return mapper.entityToDetail(team);
    }

    public TeamDetailDTO updateTeam(UpdateTeamDTO updateDTO, String userId) {
        Team team = getIfLeader(updateDTO.teamId(), userId);

        Team updated = domainService.saveTeam(
                team.toBuilder()
                        .teamName((updateDTO.teamName() == null || updateDTO.teamName().isEmpty())
                                ? team.getTeamName()    //참(비었으면)
                                : updateDTO.teamName()) //거짓(값이 있으면)
                        .description((updateDTO.description() == null || updateDTO.description().isEmpty())
                                ? team.getDescription()
                                : updateDTO.description())
                        .build()
        );

        return mapper.entityToDetail(updated);
    }

    public void updateSubLeader(UpdateSubLeaderDTO updateDTO, String leaderId) {
        Team team = getIfLeader(updateDTO.teamId(), leaderId);
        User newSubLeader = userDomainService.getReference(updateDTO.subLeaderId());
        domainService.saveTeam(team.toBuilder()
                .subLeader(newSubLeader)
                .build());
    }

    public void deleteTeam(String teamId, String userId) {
        Team team = getIfLeader(teamId, userId);
        domainService.deleteTeam(team);
    }


    //Todo later: Elastic Search 로 변경
    public SearchResultDTO searchTeamByKeyword(String searchKeyword, Pageable pageable) {
        List<Team> teamList = domainService.searchByName(searchKeyword);
        List<TeamNameDTO> dtoList = mapper.entityListToDto(teamList);
        return new SearchResultDTO(dtoList);
    }

     public SearchResultDTO searchTeamByUser(String userId) {
      List<UUID> teamIds = memberDomainService.findTeamIdsByUser(userId);
      List<Team> teams = domainService.getAllById(teamIds);
      List<TeamNameDTO> dtoList = mapper.entityListToDto(teams);
      return new SearchResultDTO(dtoList);
  }


    ///*********** Verifier ***********///

    /**
     * Jwt 토큰에서 받아온 유저가 유효한지 검사
     *
     */
    private void verifyUser(String userId) {
        if (!userDomainService.isUserExist(userId)) {
            throw new ApplicationException(ErrorCode.INVALID_USER);
        }
    }


    /**
     * 팀 리더인지 판단
     *
     * @param teamId 가져올 팀
     * @param userId 팀 리더 일때 return
     * @return Team 엔티티
     *
     */
    private Team getIfLeader(String teamId, String userId) {
        Team team = domainService.getReference(teamId);
        if (team.getLeader().getUserId().toString().equals(userId)) {
            return team;
        }
        throw new ApplicationException(ErrorCode.NOT_ALLOWED);
    }

}