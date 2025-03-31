package com.ohgiraffers.COZYbe.domain.teams.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID teamId;

    @Column
    private String teamName;

    @Column(length = 2000)
    private String description;

}
