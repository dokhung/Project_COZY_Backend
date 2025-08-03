package com.ohgiraffers.COZYbe.domain.plan.controller;

import com.ohgiraffers.COZYbe.domain.plan.dto.PlanDto;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // 게시글 등록
    @PostMapping("/create")
    public ResponseEntity<Plan> createPlan(@RequestBody PlanDto planDto) {
        System.out.println("planDto : " + planDto);
        return ResponseEntity.ok(planService.createPlan(planDto));
    }

    // 게시글 정보를 볼수있다. 게시판의 내용을 클릭하여 볼때 한번 더 보낸다.
    @GetMapping("/list")
    public ResponseEntity<List<Plan>> getPlanList() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    // 게시글 안에서 유저의 정보를 불러와서 관련된 내용을 수정합니다.
    @PutMapping("/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id, @RequestBody PlanDto planDto) {
        Plan updatedPlan = planService.updatePlan(id, planDto);
        return ResponseEntity.ok(updatedPlan);
    }

    // 게시글 안에서 유저의 정보를 불러와서 관련된 내용을 삭제합니다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글의 대한 내용을 상세하게 보여주기 위함
    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlanById(@PathVariable Long id) {
        Plan plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/by-nickname")
    public ResponseEntity<List<Plan>> getPlansByNickname(@RequestParam String nickname) {
        List<Plan> plans = planService.getPlanByNickName(nickname);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<Plan>> getMyPlans(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<Plan> plans = planService.getPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }






}
