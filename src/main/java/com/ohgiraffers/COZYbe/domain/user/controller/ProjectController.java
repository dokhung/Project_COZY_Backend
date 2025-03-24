package com.ohgiraffers.COZYbe.domain.user.controller;

import com.ohgiraffers.COZYbe.domain.user.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/check-projectname")
    public ResponseEntity<?> checkProjectName(@RequestParam String projectName){
        boolean available = projectService.inProjectNameAvailable(projectName);
        return ResponseEntity.ok(Map.of("available", available));
    }
}
