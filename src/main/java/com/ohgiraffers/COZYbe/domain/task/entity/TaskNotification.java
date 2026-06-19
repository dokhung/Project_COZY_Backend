package com.ohgiraffers.COZYbe.domain.task.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_task_notification")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNotification extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User recipient;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Task task;
    @Column(nullable = false, length = 50)
    private String type;
    @Column(nullable = false, length = 500)
    private String message;
    @Column(nullable = false)
    private boolean read;
}
