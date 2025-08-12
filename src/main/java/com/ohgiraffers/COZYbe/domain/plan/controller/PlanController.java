package com.ohgiraffers.COZYbe.domain.plan.controller;

import com.ohgiraffers.COZYbe.domain.plan.dto.PlanDto;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.plan.service.PlanService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public PlanController(PlanService planService) { this.planService = planService; }

    @PostMapping("/create")
    public ResponseEntity<Plan> createPlan(@Valid @RequestBody PlanDto planDto,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(planService.createPlan(planDto, userId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Plan>> getPlanList() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id, @RequestBody PlanDto planDto) {
        return ResponseEntity.ok(planService.updatePlan(id, planDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @GetMapping("/by-nickname")
    public ResponseEntity<List<Plan>> getPlansByNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(planService.getPlanByNickName(nickname));
    }
}
