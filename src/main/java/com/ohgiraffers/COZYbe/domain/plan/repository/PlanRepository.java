package com.ohgiraffers.COZYbe.domain.plan.repository;

import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByNickname(String nickname);
}
