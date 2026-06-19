package com.ohgiraffers.COZYbe.domain.task.repository;

import com.ohgiraffers.COZYbe.domain.task.entity.TaskNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskNotificationRepository extends JpaRepository<TaskNotification, Long> {
    List<TaskNotification> findByRecipient_UserIdOrderByCreatedAtDesc(UUID userId);
    void deleteByTask_TaskId(Long taskId);
}
