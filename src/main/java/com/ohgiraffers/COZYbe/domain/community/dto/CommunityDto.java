package com.ohgiraffers.COZYbe.domain.community.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommunityDto {
    private Long id;
    private String title;
    private String nickName;
    private String communityText;
//    private LocalDateTime createdAt;
}
