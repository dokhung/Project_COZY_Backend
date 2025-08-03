package com.ohgiraffers.COZYbe.domain.projects.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_project")
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", nullable = false, unique = true, length = 100)
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "interest", nullable = false, length = 50)
    private String interest;

    // 해당 프로젝트의 게시글 연결
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Community> communityList = new ArrayList<>();

    // 일정들
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Plan> planList = new ArrayList<>();
}
