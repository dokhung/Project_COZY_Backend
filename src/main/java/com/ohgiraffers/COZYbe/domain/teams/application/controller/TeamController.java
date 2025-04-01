package com.ohgiraffers.COZYbe.domain.teams.application.controller;

import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.CreateTeamDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamListDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.response.TeamNameDTO;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamService;
import com.ohgiraffers.COZYbe.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/team")
public class TeamController {

    private TeamService service;
    private AuthService authService;

    @GetMapping("/list")
    public TeamListDTO getTeamList(){
        return service.getAllList();
    }

    @PostMapping
    public TeamNameDTO createTeam(@RequestBody CreateTeamDTO createTeamDTO, HttpServletRequest request){
        String token = authService.trimToken(request);
        String email = authService.getEmailFromToken(token);

        return service.createTeam();
    }
}
