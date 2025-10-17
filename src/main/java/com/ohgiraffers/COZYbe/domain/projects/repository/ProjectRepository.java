package com.ohgiraffers.COZYbe.domain.projects.repository;

import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findFirstByOwner(User owner);
}
