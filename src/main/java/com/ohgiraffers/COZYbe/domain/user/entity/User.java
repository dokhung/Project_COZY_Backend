package com.ohgiraffers.COZYbe.domain.user.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(name = "profile_image", length = 500)
    private String profileImageUrl;

    @Column(name = "statusMessage")
    private String statusMessage;



}
