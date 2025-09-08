package com.ohgiraffers.COZYbe.domain.member.dto.response;

import java.util.List;

public record MemberListDTO(
        List<MemberBriefDTO> memberList
) {
}
