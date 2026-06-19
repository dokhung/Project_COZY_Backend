package com.ohgiraffers.COZYbe.domain.task.repository;

import com.ohgiraffers.COZYbe.domain.task.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTask_TaskIdOrderByCreatedAtAsc(Long taskId);
    void deleteByTask_TaskId(Long taskId);
}
