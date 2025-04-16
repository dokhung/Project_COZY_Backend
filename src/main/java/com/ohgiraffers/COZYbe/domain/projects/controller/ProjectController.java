package com.ohgiraffers.COZYbe.domain.projects.controller;

import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectInterestDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.service.ProjectService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    private final JwtTokenProvider jwtTokenProvider;

    public ProjectController(ProjectService projectService, JwtTokenProvider jwtTokenProvider) {
        this.projectService = projectService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/check-projectname")
    public ResponseEntity<?> checkProjectName(@RequestParam String projectName){
        boolean available = projectService.inProjectNameAvailable(projectName);
        return ResponseEntity.ok(Map.of("available", available));
    }


    @PostMapping("/projectCreate")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectInterestDTO dto,
                                           HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtTokenProvider.decodeUserIdFromJwt(token);

        Project project = projectService.createProject(dto, email); // 🔥 반환된 엔티티 사용
        return ResponseEntity.ok(Map.of(
                "projectId", project.getProjectId(),
                "projectName", project.getProjectName()
        ));
    }

    //TODO:프로젝트 보유 유무
    @GetMapping("/my-project")
    public ResponseEntity<?> getMyProjectInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtTokenProvider.decodeUserIdFromJwt(token);
        Optional<Project> projectOpt = projectService.getProjectByUserEmail(email);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            return ResponseEntity.ok(Map.of(
                    "projectId", project.getProjectId(),
                    "projectName", project.getProjectName(),
                    "createdAt", project.getCreatedAt()
            ));
        }else{
            return ResponseEntity.ok(Map.of("message", "Project not found"));
        }
    }

}
