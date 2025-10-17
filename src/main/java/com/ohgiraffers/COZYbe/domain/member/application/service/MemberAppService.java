package com.ohgiraffers.COZYbe.domain.member.application.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.application.dto.response.MemberListDTO;
import com.ohgiraffers.COZYbe.domain.member.domain.entity.Member;
import com.ohgiraffers.COZYbe.domain.member.domain.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.member.domain.service.MemberDomainService;
import com.ohgiraffers.COZYbe.domain.teams.application.service.TeamAppService;
import com.ohgiraffers.COZYbe.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberAppService {

    private final MemberDomainService domainService;
    private final TeamAppService teamAppService;
    private final UserService userService;
    private final MemberMapper mapper;

    private final MemberRepository repository;  //deleting

    public MemberListDTO getMemberList(String teamId) {
        if (!teamAppService.isTeamExist(teamId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }
        List<Member> members = domainService.findByTeam(teamId);
        return new MemberListDTO(mapper.entityListToDto(members));
    }

    public void joinMember(String teamId, String userId) {
        if (!teamAppService.isTeamExist(teamId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_TEAM);
        }
        if (!userService.isUserExist(userId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }
        Member newMember = Member.builder()
                .team(teamAppService.findById(teamId))
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
