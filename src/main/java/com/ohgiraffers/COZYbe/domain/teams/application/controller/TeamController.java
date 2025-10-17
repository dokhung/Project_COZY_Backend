package com.ohgiraffers.COZYbe.domain.teams.application.controller;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateSubLeaderDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.UpdateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.SearchResultDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamDetailDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamAppService teamAppService;

    @GetMapping("/list")
    public ResponseEntity<?> getTeamList(){
        return ResponseEntity.ok(teamAppService.getAllList());
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamDTO createTeamDTO,
                                     @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamAppService.createTeam(createTeamDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @GetMapping
    public ResponseEntity<?> getTeam(@RequestParam(value = "team") String teamId,
                        @AuthenticationPrincipal Jwt jwt){
        log.info("Request team detail by ID : {}", teamId);
        TeamDetailDTO detailDTO = teamAppService.getTeamDetail(teamId, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @PatchMapping
    public ResponseEntity<?> updateTeam(@RequestBody UpdateTeamDTO updateDTO,
                                                    @AuthenticationPrincipal Jwt jwt){
        TeamDetailDTO detailDTO = teamAppService.updateTeam(updateDTO, jwt.getSubject());
        return ResponseEntity.ok(detailDTO);
    }

    @DeleteMapping
    public ResponseEntity<?>  deleteTeam(@RequestParam(value = "team") String teamId,
                           @AuthenticationPrincipal Jwt jwt){
        teamAppService.deleteTeam(teamId, jwt.getSubject());
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
        SearchResultDTO resultDTO = teamAppService.searchTeamByUser(jwt.getSubject());
        return ResponseEntity.ok(resultDTO);
    }

    @GetMapping("/check-team-name")
    public ResponseEntity<?> checkTeamName(@RequestParam String teamName){
        boolean isNameExist = teamAppService.checkTeamNameExist(teamName);
        return ResponseEntity.ok(isNameExist);
    }

    @PatchMapping("/sub-leader")
    public ResponseEntity<?> updateSubLeader(@RequestBody UpdateSubLeaderDTO updateDTO,
                                             @AuthenticationPrincipal Jwt jwt) {
        teamAppService.updateSubLeader(updateDTO, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

}
