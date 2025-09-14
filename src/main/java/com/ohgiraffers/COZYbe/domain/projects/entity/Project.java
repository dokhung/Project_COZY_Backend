package com.ohgiraffers.COZYbe.domain.projects.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.task.entity.Task;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_project")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectId")
    private Long projectId;

    @Column(name = "projectName", nullable = false, unique = true, length = 100)
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;

    @Column(name = "devInterest", nullable = false, length = 50)
    private String devInterest;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "leader_name", nullable = false, length = 100)
    private String leaderName;

    @Column(name = "gitHubUrl", nullable = true, length = 800)
    private String gitHubUrl;


    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}
