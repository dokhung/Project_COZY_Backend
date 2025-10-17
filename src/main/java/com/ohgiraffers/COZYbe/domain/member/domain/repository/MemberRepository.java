package com.ohgiraffers.COZYbe.domain.member.domain.repository;

import com.ohgiraffers.COZYbe.domain.member.domain.entity.Member;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<List<Member>> findByTeam_TeamId(UUID teamId);

    Optional<Member> findByTeam_TeamIdAndUser_UserId(UUID teamId, UUID userId);

    Optional<List<Member>> findByUser_UserId(UUID userId);

    Optional<List<Member>> finByUser(User user);

    Optional<List<Member>> findByTeam(Team team);
}
