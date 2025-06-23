package com.ohgiraffers.COZYbe.domain.teams.application.controller;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.SearchResultDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamService;
import jdk.jfr.Unsigned;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/list")
    public ResponseEntity<?> getTeamList(){
        return ResponseEntity.ok(teamService.getAllList());
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamDTO createTeamDTO,
                                     @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamService.createTeam(createTeamDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @GetMapping
    public ResponseEntity<?> getTeam(@RequestParam(value = "team") String teamId,
                        @AuthenticationPrincipal Jwt jwt){
        log.info("Request team detail by ID : {}", teamId);
        TeamDetailDTO detailDTO = teamService.getTeamDetail(teamId, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @PatchMapping
    public ResponseEntity<?> updateTeam(@RequestBody UpdateTeamDTO updateDTO,
                                                    @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamService.updateTeam(updateDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @DeleteMapping
    public ResponseEntity<?>  deleteTeam(@RequestParam(value = "team") String teamId,
                           @AuthenticationPrincipal Jwt jwt){
        teamService.deleteTeam(teamId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    //미완성
//    @GetMapping("/search")
//    public ResponseEntity<?> findTeamByName(@RequestParam(value = "search") String searchKeyword, Pageable pageable){
//        log.info("search keyword : {}", searchKeyword);
//        SearchResultDTO resultDTO = teamService.searchTeamByKeyword(searchKeyword, pageable);
//        return ResponseEntity.ok(resultDTO);
//    }

    @GetMapping("/my-team")
    public ResponseEntity<?> findMyTeam(@AuthenticationPrincipal Jwt jwt){
        SearchResultDTO resultDTO = teamService.searchTeamByUser(jwt.getSubject());
        return ResponseEntity.ok(resultDTO);
    }

}
