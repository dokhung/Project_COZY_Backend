package com.ohgiraffers.COZYbe.domain.task.repository;

import com.ohgiraffers.COZYbe.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// select from Task
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProject_ProjectId(Long projectId);


}
