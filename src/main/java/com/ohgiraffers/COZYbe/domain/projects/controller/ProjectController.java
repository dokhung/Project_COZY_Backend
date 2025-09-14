package com.ohgiraffers.COZYbe.domain.projects.controller;
import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectDTO;
import com.ohgiraffers.COZYbe.domain.projects.dto.UpdateProjectDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.service.ProjectService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
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
            throw new RuntimeException("Not Token.");
        }
        return jwtTokenProvider.decodeUserIdFromJwt(token.substring(7));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId, HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = auth.substring(7);
        String userId = jwtTokenProvider.decodeUserIdFromJwt(token);

        projectService.deleteProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long projectId,
                                           @RequestBody @Valid UpdateProjectDTO dto,
                                           HttpServletRequest req) {
        String userId = extractUserId(req);
        Project p = projectService.updateProject(dto, projectId, userId);
        return ResponseEntity.ok(Map.of(
                "projectId", p.getProjectId(),
                "projectName", p.getProjectName(),
                "description", p.getDescription(),
                "devInterest", p.getDevInterest(),
                "githubUrl", p.getGitHubUrl(),
                "ownerId", p.getOwner().getUserId().toString(),
                "ownerName", p.getLeaderName(),
                "createdAt", p.getCreatedAt()
        ));
    }



    @GetMapping("/detail/{projectName}")
    public ResponseEntity<?> getProjectDetail(@PathVariable String projectName,
                                              HttpServletRequest request) {
        String userId = extractUserId(request);
        Project project = projectService.getProjectDetailForUser(projectName, userId);

        return ResponseEntity.ok(Map.of(
                "projectId", project.getProjectId(),
                "projectName", project.getProjectName(),
                "description", project.getDescription(),
                "devInterest", project.getDevInterest(),
                "gitHubUrl", project.getGitHubUrl(),
                "ownerId", project.getOwner().getUserId().toString(),
                "ownerName", project.getLeaderName(),
                "createdAt", project.getCreatedAt()
        ));
    }



}

