package com.ohgiraffers.COZYbe.domain.projects.controller;

import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectInterestDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.service.ProjectService;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    private final JwtTokenProvider jwtTokenProvider;

    public ProjectController(ProjectService projectService, JwtTokenProvider jwtTokenProvider) {
        this.projectService = projectService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //TODO : 프로젝트의 이름을 중복체크하고 없다면 저장한다.
    @GetMapping("/check-projectname")
    public ResponseEntity<?> checkProjectName(@RequestParam String projectName){
        boolean available = projectService.inProjectNameAvailable(projectName);
        log.info("프로젝트 중복 여부 :: " + available);
        return ResponseEntity.ok(Map.of("available", available));
    }

    //TODO : 주작업이 뭔지 알고 싶은 때를 선택한다.
    @PostMapping("/projectCreate")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectInterestDTO dto,
                                           HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String userId = jwtTokenProvider.decodeUserIdFromJwt(token);
        Project project = projectService.createProject(dto, userId);
        return ResponseEntity.ok(Map.of(
                "projectId", project.getProjectId(),
                "projectName", project.getProjectName()
        ));
    }


    @GetMapping("/my-projectInfo")
    public ResponseEntity<?> getMyProjectInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "인증 토큰 없음"));
        }

        String userId = jwtTokenProvider.decodeUserIdFromJwt(token);
        Optional<Project> projectOpt = projectService.getProjectByUserId(userId);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            return ResponseEntity.ok(Map.of(
                    "hasProject", true,
                    "projectId", project.getProjectId(),
                    "projectName", project.getProjectName(),
                    "createdAt", project.getCreatedAt()
            ));
        } else {
            return ResponseEntity.ok(Map.of("hasProject", false));
        }
    }




    @GetMapping("/name/{projectName}")
    public ResponseEntity<?> getProjectByName(@PathVariable String projectName) {
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            return ResponseEntity.ok(Map.of(
                    "projectId", project.getProjectId(),
                    "projectName", project.getProjectName(),
//                    "interest", project.getInterest(),  <== 임시
                    "createdAt", project.getCreatedAt()
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found"));
        }
    }

}
