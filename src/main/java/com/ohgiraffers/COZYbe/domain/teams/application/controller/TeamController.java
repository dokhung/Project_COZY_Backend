package com.ohgiraffers.COZYbe.domain.teams.application.controller;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.TeamIdDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamService;
import com.ohgiraffers.COZYbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/list")
    public List<TeamNameDTO> getTeamList(){
        return teamService.getAllList();
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamDTO createTeamDTO,
                                     @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamService.createTeam(createTeamDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @GetMapping
    public ResponseEntity<?> getTeam(@RequestBody TeamIdDTO teamIdDTO,
                        @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamService.getTeamDetail(teamIdDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @PatchMapping
    public ResponseEntity<?> updateTeam(@RequestBody UpdateTeamDTO updateDTO,
                                                    @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamService.updateTeam(updateDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @DeleteMapping
    public ResponseEntity<?>  deleteTeam(@RequestBody TeamIdDTO teamIdDTO,
                           @AuthenticationPrincipal Jwt jwt){
        teamService.deleteTeam(teamIdDTO, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public void findTeamByName(){

    }

    @GetMapping("/my-team")
    public void findMyTeam(@AuthenticationPrincipal Jwt jwt){

    }

}
