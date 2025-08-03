package com.ohgiraffers.COZYbe.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlanDto {
    private Long id;
    private String title;
    private String status;
    private String nickName;
    private String planText;
    private Long projectId;
}
