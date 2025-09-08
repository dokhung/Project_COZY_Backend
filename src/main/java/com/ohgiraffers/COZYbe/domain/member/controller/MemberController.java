package com.ohgiraffers.COZYbe.domain.member.controller;

import com.ohgiraffers.COZYbe.domain.member.dto.response.MemberListDTO;
import com.ohgiraffers.COZYbe.domain.member.service.MemberService;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.TeamIdDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService service;


    @GetMapping("/list")
    public void getMemberlist(TeamIdDTO teamIdDTO,
                           @AuthenticationPrincipal Jwt jwt){
        MemberListDTO dto = service.getMemberList(teamIdDTO.teamId());

    }

    @PostMapping
    public void memberJoin(){

    }

    @DeleteMapping
    public void memberLeave(){

    }

    @GetMapping("/find-join")
    public void isMemberJoined(){

    }

}
