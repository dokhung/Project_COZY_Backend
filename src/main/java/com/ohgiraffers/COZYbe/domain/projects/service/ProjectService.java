package com.ohgiraffers.COZYbe.domain.projects.service;

import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectInterestDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //TODO:프로젝트이름 중복 확인을 트루 펄스로 보냄
    public boolean inProjectNameAvailable(String projectName){
        return projectRepository.findByProjectName(projectName).isEmpty();
    }

    //TODO:프로젝트 등록
    public Project createProject(CreateProjectInterestDTO dto, String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없다. " + userId));


        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .owner(user)
                .interest(dto.getInterest())
                .build();
        projectRepository.save(project);
        return projectRepository.save(project);
    }

    //TODO: 프로젝트정보
    public Optional<Project> getProjectByUserId(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return projectRepository.findFirstByOwner(user);
    }


    public Optional<Project> getProjectByName(String projectName) {
        return projectRepository.findByProjectName(projectName);
    }

    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }
}
