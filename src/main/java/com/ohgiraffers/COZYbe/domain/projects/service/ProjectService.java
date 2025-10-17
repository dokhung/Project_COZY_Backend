package com.ohgiraffers.COZYbe.domain.projects.service;

import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectDTO;
import com.ohgiraffers.COZYbe.domain.projects.dto.UpdateProjectDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import com.ohgiraffers.COZYbe.domain.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public boolean isProjectNameAvailable(String projectName) {
        return projectRepository.findByProjectName(projectName).isEmpty();
    }

    // ProjectService#createProject
    public Project createProject(CreateProjectDTO dto, String userId) {
        User user = findUserById(userId);

        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .devInterest(dto.getDevInterest())
                .description(dto.getDescription())
                .leaderName(dto.getLeaderName())
                .gitHubUrl(dto.getGitHubUrl())
                .owner(user)
                .build();


        return projectRepository.save(project);
    }




    public Project getProjectByUserId(String userId) {
        User user = findUserById(userId);

        return projectRepository.findFirstByOwner(user)
                .orElseThrow(() -> new RuntimeException("프로젝트가 없습니다."));
    }

    public Project getProjectByNameForUser(String projectName, String userId) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        if (!project.getOwner().getUserId().toString().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 프로젝트만 조회할 수 있습니다.");
        }
        return project;
    }

    private User findUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public void deleteProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + projectId));

        if (!project.getOwner().getUserId().toString().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this project.");
        }

        projectRepository.delete(project);
    }



    @Transactional
    public Project updateProject(UpdateProjectDTO dto, Long projectId, String userId) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        if (!p.getOwner().getUserId().toString().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        p.setProjectName(dto.getProjectName());
        p.setDevInterest(dto.getDevInterest());
        p.setDescription(dto.getDescription());
        p.setGitHubUrl(dto.getGitHubUrl());
        if (dto.getLeaderName() != null) p.setLeaderName(dto.getLeaderName());

        return projectRepository.save(p);
    }


    public Project getProjectDetailForUser(String projectName, String userId) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        if (!project.getOwner().getUserId().toString().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 프로젝트만 조회할 수 있습니다.");
        }
        return project;
    }



}
