package com.ohgiraffers.COZYbe.domain.member.controller;

import com.ohgiraffers.COZYbe.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private MemberService memberService;


    @GetMapping
    public void memberList(){

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
