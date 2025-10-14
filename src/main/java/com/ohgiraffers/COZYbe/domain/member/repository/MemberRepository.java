package com.ohgiraffers.COZYbe.domain.member.repository;

import com.ohgiraffers.COZYbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<List<Member>> findByTeam_TeamId(UUID teamId);

    Optional<Member> findByTeam_TeamIdAndUser_UserId(UUID teamId, UUID userId);
}
