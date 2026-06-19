package com.ohgiraffers.COZYbe.domain.task.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_task_attachment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;
    @Column(nullable = false, length = 255)
    private String originalName;
    @Column(nullable = false, length = 500)
    private String storageKey;
    @Column(length = 150)
    private String contentType;
    @Column(nullable = false)
    private long size;
}
