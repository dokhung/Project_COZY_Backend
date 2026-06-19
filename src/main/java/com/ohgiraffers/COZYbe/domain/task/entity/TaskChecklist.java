package com.ohgiraffers.COZYbe.domain.task.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_task_checklist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checklistId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @Column(nullable = false, length = 500)
    private String content;
    @Column(nullable = false)
    private boolean completed;
    @Column(nullable = false)
    private Long position;
}
