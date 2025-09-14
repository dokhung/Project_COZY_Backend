package com.ohgiraffers.COZYbe.domain.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectDTO {

    // 프로젝트 이름
    private String projectName;
    // 프로젝트 종류
    private String devInterest;
    // 프로젝트 설명
    private String description;
    // プロジェクトマスター
    private String leaderName;
    // gitHubUrl
    private String gitHubUrl;
}

