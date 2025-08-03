package com.ohgiraffers.COZYbe.domain.plan.service;

import com.ohgiraffers.COZYbe.domain.plan.dto.PlanDto;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.plan.repository.PlanRepository;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public PlanService(PlanRepository planRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    // 게시판글의 대한 내용을 프론트엔드에 보내기 위함
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    // 게시판 디비에 등록함
    public Plan createPlan(PlanDto planDto) {
        Project project = projectRepository.findById(planDto.getProjectId())
                .orElseThrow(()-> new RuntimeException("Project not found"));
        Plan plan = Plan.builder()
                .title(planDto.getTitle())
                .nickname(planDto.getNickName())
                .status(planDto.getStatus())
                .planText(planDto.getPlanText())
                .project(project)
                .build();
        return planRepository.save(plan);
    }


    // 삭제
    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id).orElseThrow(()->
                new RuntimeException("게시글이 없어요"));
        planRepository.delete(plan);
    }

    // 수정
    public Plan updatePlan(Long id, PlanDto planDto) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 없어요."));
        plan.setTitle(planDto.getTitle());
        plan.setPlanText(planDto.getPlanText());
        plan.setStatus(planDto.getStatus());
        return planRepository.save(plan);
    }

    // 내용상세
    public Plan getPlanById(Long id) {
        return planRepository.findById(id).orElseThrow(()-> new RuntimeException("게시글이 없다."));
    }

    // 닉네임으로 게시글을 조회한다.
    public List<Plan> getPlanByNickName(String nickname) {
        return planRepository.findAllByNickname(nickname);
    }

    public List<Plan> getPlansByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없다."));
        return planRepository.findAllByNickname(user.getNickname());
    }
}
