package com.ohgiraffers.COZYbe.domain.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlanDto {
    @NotBlank private String nickName;
    @NotBlank private String title;
    @NotBlank private String status;
    @NotBlank private String planText;
    @NotNull  private Long projectId;
}
