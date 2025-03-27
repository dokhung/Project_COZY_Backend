package com.ohgiraffers.COZYbe.domain.user.repository;

import com.ohgiraffers.COZYbe.domain.user.entity.Project;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findFirstByOwner(User owner);
}
