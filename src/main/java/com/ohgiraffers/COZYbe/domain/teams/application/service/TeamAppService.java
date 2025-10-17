package com.ohgiraffers.COZYbe.domain.teams.application.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.application.service.MemberAppService;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.SearchResultDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.teams.domain.repository.TeamRepository;
import com.ohgiraffers.COZYbe.domain.teams.domain.service.TeamDomainService;
import com.ohgiraffers.COZYbe.domain.user.application.service.UserAppService;
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

    private final TeamRepository repository;
    private final TeamDomainService domainService;
    private final TeamMapper mapper;

    private final MemberAppService memberAppService;
    private final UserAppService userAppService;


    public SearchResultDTO getAllList() {
        List<Team> teams = repository.findAll();
        return new SearchResultDTO(mapper.entityListToDto(teams));
    }

    @Transactional
    public TeamDetailDTO createTeam(CreateTeamDTO createTeamDTO, String userId) {
        Team newTeam = Team.builder()
                .teamName(createTeamDTO.teamName())
                .description(createTeamDTO.description())
                .leader(userAppService.getReference(userId))
                .build();

        Team created = repository.save(newTeam);
        log.info("팀 생성됨 : {}",created.getTeamName());
        memberAppService.joinMember(String.valueOf(created.getTeamId()),userId);
        return mapper.entityToDetail(created);
    }


    public Team findById(String teamId){
        return repository.findById(UUID.fromString(teamId))
                .orElseThrow(()->new ApplicationException(ErrorCode.NO_SUCH_TEAM));
    }

    public TeamDetailDTO getTeamDetail(String teamId, String userId) {
        verifyUser(userId);

        Team team= repository.findById(UUID.fromString(teamId))
                .orElseThrow(()-> new ApplicationException(ErrorCode.NO_SUCH_TEAM));
        return mapper.entityToDetail(team);
    }

    public TeamDetailDTO updateTeam(UpdateTeamDTO updateDTO, String userId) {
        if (updateDTO.teamId() == null || updateDTO.teamId().isEmpty()){
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }

        Team team = getIfLeader(updateDTO.teamId(), userId);

        String updateTeamName, updateDescription;
        if (updateDTO.teamName() == null || updateDTO.teamName().isEmpty()){
            updateTeamName = team.getTeamName();
        }else{
            updateTeamName = updateDTO.teamName();
        }
        if (updateDTO.description() == null || updateDTO.description().isEmpty()){
            updateDescription = team.getDescription();
        }else {
            updateDescription = updateDTO.description();
        }

        Team updated = repository.save(
                Team.builder()
                .teamId(team.getTeamId())
                .teamName(updateTeamName)
                .description(updateDescription)
                .leader(team.getLeader())
                .build()
        );

        return mapper.entityToDetail(updated);
    }

    public void deleteTeam(String teamId, String userId) {
        Team team = getIfLeader(teamId, userId);
        repository.delete(team);
    }


    //Todo later: Elastic Search 로 변경
    public SearchResultDTO searchTeamByKeyword(String searchKeyword, Pageable pageable) {
        List<Team> teamList= repository.findByTeamNameContainingIgnoreCase(searchKeyword);
        List<TeamNameDTO> dtoList = mapper.entityListToDto(teamList);
        return new SearchResultDTO(dtoList);
    }

    public SearchResultDTO searchTeamByUser(String userId) {
        List<Team> teamList = domainService.findByLeader(userId);
        List<TeamNameDTO> dtoList = mapper.entityListToDto(teamList);
        return new SearchResultDTO(dtoList);
    }


    ///*********** Verifier ***********///

    /**
     * Jwt 토큰에서 받아온 유저가 유효한지 검사
     * */
    private void verifyUser(String userId){
        if (!userAppService.isUserExist(userId)){
            throw new ApplicationException(ErrorCode.INVALID_USER);
        }
    }

    /**
     * 팀 리더인지 판단
     * @param teamId 가져올 팀
     * @param userId 팀 리더 일때 return
     * @return Team 엔티티
     * */
    private Team getIfLeader(String teamId, String userId){
        Team team = findById(teamId);
        if (team.getLeader().getUserId().toString().equals(userId)){
            return team;
        }
        throw new ApplicationException(ErrorCode.NOT_ALLOWED);
    }

    public boolean isTeamExist(String teamId){
        return repository.existsById(UUID.fromString(teamId));
    }
}

