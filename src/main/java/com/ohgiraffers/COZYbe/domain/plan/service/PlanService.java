package com.ohgiraffers.COZYbe.domain.plan.service;

import com.ohgiraffers.COZYbe.domain.plan.dto.PlanDto;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.plan.repository.PlanRepository;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Transactional
    public Plan createPlan(PlanDto dto, UUID userId) {
        if (dto.getProjectId() == null || dto.getProjectId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "projectId is required");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로젝트가 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        Plan plan = new Plan();
        plan.setTitle(dto.getTitle());
        plan.setPlanText(dto.getPlanText());
        plan.setStatus(dto.getStatus());
        plan.setNickname(dto.getNickName());
        plan.setProject(project);
        plan.setUser(user); // ★ 필수
        return planRepository.save(plan);
    }

    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없어요"));
        planRepository.delete(plan);
    }

    public Plan updatePlan(Long id, PlanDto planDto) {
        Plan plan = planRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없어요."));
        plan.setTitle(planDto.getTitle());
        plan.setPlanText(planDto.getPlanText());
        plan.setStatus(planDto.getStatus());
        return planRepository.save(plan);
    }

    public Plan getPlanById(Long id) {
        return planRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없다."));
    }

    public List<Plan> getPlanByNickName(String nickname) {
        return planRepository.findAllByNickname(nickname);
    }
}
