package com.ohgiraffers.COZYbe.domain.projects.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_project")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_Id")
    private Long projectId;

    @Column(name = "project_name", nullable = false, unique = true, length = 100)
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "interest", nullable = false, length = 50)
    private String interest;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "leader_name", nullable = false, length = 100)
    private String leaderName;
}

