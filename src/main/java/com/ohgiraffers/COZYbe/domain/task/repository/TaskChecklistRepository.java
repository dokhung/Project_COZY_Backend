package com.ohgiraffers.COZYbe.domain.task.repository;

import com.ohgiraffers.COZYbe.domain.task.entity.TaskChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskChecklistRepository extends JpaRepository<TaskChecklist, Long> {
    List<TaskChecklist> findByTask_TaskIdOrderByPositionAsc(Long taskId);
    void deleteByTask_TaskId(Long taskId);
}
