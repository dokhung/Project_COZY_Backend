package com.ohgiraffers.COZYbe.domain.member.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.dto.response.MemberListDTO;
import com.ohgiraffers.COZYbe.domain.member.entity.Member;
import com.ohgiraffers.COZYbe.domain.member.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamService;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberService {

    private final MemberRepository repository;
    private final TeamService teamService;
    private final UserService userService;
    private final MemberMapper mapper;


    public MemberListDTO getMemberList(String teamId) {
        if (!teamService.isTeamExist(teamId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }
        List<Member> members = repository.findByTeam_TeamId(UUID.fromString(teamId)).orElseThrow(
                () -> new ApplicationException(ErrorCode.NO_SUCH_TEAM)
        );
        return new MemberListDTO(mapper.entityListToDto(members));
    }

    public void joinMember(String teamId, String userId) {
        if (!teamService.isTeamExist(teamId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }
        if (!userService.isUserExist(userId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }
        Member newMember = Member.builder()
                .team(teamService.findById(teamId))
                .memberId(UUID.fromString(userId))
                .build();

        repository.save(newMember);
    }

    public void leaveMember(String teamId, String userId) {
        Member member = repository.findByTeam_TeamIdAndUser_UserId(
                UUID.fromString(teamId), UUID.fromString(userId)
                )
                .orElseThrow(()-> new ApplicationException(ErrorCode.NO_SUCH_USER)
        );
        repository.delete(member);
    }
}
