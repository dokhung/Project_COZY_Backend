package com.ohgiraffers.COZYbe.domain.community.repository;

import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findAllByNickname(String nickname);
}
