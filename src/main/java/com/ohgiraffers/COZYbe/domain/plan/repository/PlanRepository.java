package com.ohgiraffers.COZYbe.domain.plan.repository;

import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByNickname(String nickname);

    @Transactional
    void deleteAllByUser_UserId(UUID userId);
}
