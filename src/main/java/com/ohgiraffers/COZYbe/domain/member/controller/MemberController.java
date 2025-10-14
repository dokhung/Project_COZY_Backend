package com.ohgiraffers.COZYbe.domain.member.controller;

import com.ohgiraffers.COZYbe.domain.member.dto.response.MemberListDTO;
import com.ohgiraffers.COZYbe.domain.member.service.MemberService;
import com.ohgiraffers.COZYbe.domain.teams.application.dto.request.TeamIdDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService service;


    @GetMapping("/list")
    public ResponseEntity<?> getMemberlist(TeamIdDTO teamIdDTO,
                           @AuthenticationPrincipal Jwt jwt){
        MemberListDTO dto = service.getMemberList(teamIdDTO.teamId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> joinMember(TeamIdDTO teamIdDTO,
                                        @AuthenticationPrincipal Jwt jwt){
        service.joinMember(teamIdDTO.teamId(),jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> leaveMember(TeamIdDTO teamIdDTO,
                            @AuthenticationPrincipal Jwt jwt){
        service.leaveMember(teamIdDTO.teamId(),jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-join")
    public void isMemberJoined(){

    }


}
