package com.ohgiraffers.COZYbe.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectInterestDTO {
    private String projectName;
    private String interest;
}
