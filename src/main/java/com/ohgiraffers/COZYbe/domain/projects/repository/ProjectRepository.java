package com.ohgiraffers.COZYbe.domain.projects.repository;

import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findFirstByOwner(User owner);
    @Transactional
    void deleteAllByOwner_UserId(UUID userId);
}
