package com.ohgiraffers.COZYbe.domain.task.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_task")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Long taskId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Convert(converter = TaskStatusConverter.class)
    @Column(name = "status", nullable = false, length = 30)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private TaskPriority priority;

    @Column(name = "task_text", length = 4000)
    private String taskText;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "tbl_task_assignee",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> assignees = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @Column(name = "board_position", nullable = false)
    @Builder.Default
    private Long boardPosition = 0L;

    @Column(name = "recurrence_type", length = 20)
    private String recurrenceType;

    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval;

    @Column(name = "recurrence_until")
    private LocalDate recurrenceUntil;

    @PrePersist
    @PreUpdate
    private void applyDefaults() {
        if (status == null) {
            status = TaskStatus.TODO;
        }
        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }
        if (boardPosition == null) {
            boardPosition = 0L;
        }
        if (creator == null) {
            creator = user;
        }
        if (assignees.isEmpty() && user != null) {
            assignees.add(user);
        }
    }

    @PostLoad
    private void applyLegacyDefaults() {
        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }
        if (assignees.isEmpty() && user != null) {
            assignees.add(user);
        }
    }
}
