package com.ohgiraffers.COZYbe.domain.projects.controller;
import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.service.ProjectService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/check-projectname")
    public ResponseEntity<?> checkProjectName(@RequestParam String projectName) {
        boolean available = projectService.isProjectNameAvailable(projectName);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @PostMapping("/project-create")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectDTO dto,
                                           HttpServletRequest request) {
//        System.out.println("dto :: " + dto.toString());
        String userId = extractUserId(request);
        Project project = projectService.createProject(dto, userId);
        return ResponseEntity.ok(Map.of(
                "id", project.getProjectId(),
                "projectName", project.getProjectName()
        ));
    }

    @GetMapping("/my-projectInfo")
    public ResponseEntity<?> getMyProjectInfo(HttpServletRequest request) {
        String userId = extractUserId(request);
        Project project = projectService.getProjectByUserId(userId);

        return ResponseEntity.ok(Map.of(
                "projectId", project.getProjectId(),
                "hasProject", true,
                "projectName", project.getProjectName(),
                "leader", project.getLeaderName(),
                "description", project.getDescription(),
                "createdAt", project.getCreatedAt()
        ));
    }

    @GetMapping("/name/{projectName}")
    public ResponseEntity<?> getProjectByName(@PathVariable String projectName,
                                              HttpServletRequest request) {
        String userId = extractUserId(request);
        Project project = projectService.getProjectByNameForUser(projectName, userId);

        return ResponseEntity.ok(Map.of(
                "projectId", project.getProjectId(),
                "projectName", project.getProjectName(),
                "createdAt", project.getCreatedAt()
        ));
    }

    private String extractUserId(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("인증 토큰이 없습니다.");
        }
        return jwtTokenProvider.decodeUserIdFromJwt(token.substring(7));
    }

    @DeleteMapping("/{proejctId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long proejctId, HttpServletRequest request) {
        String userId = extractUserId(request);
        projectService.deleteProject(proejctId,userId);
        return ResponseEntity.ok(Map.of("message", "프로젝트 및 관련 데이터가 삭제되었습니다."));
    }
}

