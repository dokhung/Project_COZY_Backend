package com.ohgiraffers.COZYbe.domain.user.entity;

import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status_message", length = 255)
    private String statusMessage;

    // 사용자가 소유한 프로젝트
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> ownedProjects = new ArrayList<>();

    // 사용자가 작성한 커뮤니티 글
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> communities = new ArrayList<>();

    // 사용자가 작성한 계획
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();
}
