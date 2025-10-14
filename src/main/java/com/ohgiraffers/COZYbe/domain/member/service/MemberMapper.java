package com.ohgiraffers.COZYbe.domain.member.service;

import com.ohgiraffers.COZYbe.domain.member.dto.response.MemberBriefDTO;
import com.ohgiraffers.COZYbe.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(source = "user.nickname", target = "memberName")
    MemberBriefDTO entityToDto(Member member);

    List<MemberBriefDTO> entityListToDto(List<Member> members);
}
