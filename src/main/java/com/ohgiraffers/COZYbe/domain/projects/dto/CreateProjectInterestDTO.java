package com.ohgiraffers.COZYbe.domain.projects.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateProjectInterestDTO {
    private String projectName;
    private String interest;
}
