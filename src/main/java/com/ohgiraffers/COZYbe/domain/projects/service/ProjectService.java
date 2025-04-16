package com.ohgiraffers.COZYbe.domain.projects.service;

import com.ohgiraffers.COZYbe.domain.projects.dto.CreateProjectInterestDTO;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    //TODO:프로젝트이름 중복 확인
    public boolean inProjectNameAvailable(String projectName){
        return !projectRepository.findByProjectName(projectName).isEmpty();
    }

    //TODO:프로젝트 등록
    public Project createProject(CreateProjectInterestDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .owner(user)
                .interest(dto.getInterest()) // 관심사 추가
                .build();

        return projectRepository.save(project); // ✅ 저장 후 바로 리턴
    }

    //TODO: ProjectInfo
    public Optional<Project> getProjectByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return projectRepository.findFirstByOwner(user);
    }

}
