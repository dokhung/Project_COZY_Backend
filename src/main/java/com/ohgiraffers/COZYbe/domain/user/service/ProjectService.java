package com.ohgiraffers.COZYbe.domain.user.service;

import com.ohgiraffers.COZYbe.domain.user.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    //TODO:프로젝트이름 중복 확인
    public boolean inProjectNameAvailable(String projectName){
        return !projectRepository.findByProjectName(projectName).isEmpty();
    }
}
