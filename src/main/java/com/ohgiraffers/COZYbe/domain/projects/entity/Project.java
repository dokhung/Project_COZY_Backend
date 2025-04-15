package com.ohgiraffers.COZYbe.domain.projects.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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
    private Long projectId;

    @Column(nullable = false, unique = true)
    private String projectName;

    //TODO: 프로젝트의 User와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    //TODO:관심사
    @Column(nullable = false)
    private String interest;
}
