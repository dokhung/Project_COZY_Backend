package com.ohgiraffers.COZYbe.domain.community.repository;

import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
//    List<Community> findAllByNickname(String nickname);
//    @Transactional
//    void deleteAllByProject_ProjectId(Long projectId);
//
//    @Transactional
//    void deleteAllByUser_UserId(UUID userId);
//
//    @Transactional
//    void deleteAllByProject_ProjectIdIn(List<Long> projectIds);
}
