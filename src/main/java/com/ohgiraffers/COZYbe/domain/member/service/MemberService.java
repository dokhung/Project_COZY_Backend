package com.ohgiraffers.COZYbe.domain.member.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.dto.response.MemberListDTO;
import com.ohgiraffers.COZYbe.domain.member.entity.Member;
import com.ohgiraffers.COZYbe.domain.member.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberService {

    private final MemberRepository repository;
    private final TeamService teamService;
    private final MemberMapper mapper;


    public MemberListDTO getMemberList(String teamId) {
        if (!teamService.isTeamExist(teamId)){
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }
        List<Member> members = repository.findByTeamId(UUID.fromString(teamId)).orElseThrow(
                () -> new ApplicationException(ErrorCode.NO_SUCH_TEAM)
        );
        return null;
    }
}
