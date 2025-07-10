package com.ohgiraffers.COZYbe.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String title;
    private String status;
    private String nickName;
    private String postText;
    private LocalDateTime createdAt;
}

