package com.ohgiraffers.COZYbe.domain.task.repository;

import com.ohgiraffers.COZYbe.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProject_ProjectId(UUID projectId);
    List<Task> findAllByProject_ProjectIdOrderByStatusAscBoardPositionAsc(UUID projectId);
    List<Task> findDistinctByAssignees_UserIdOrderByDueDateAsc(UUID userId);
    List<Task> findByParentTask_TaskId(Long taskId);
//    Optional<Task> findByTaskId(UUID taskId);
}
