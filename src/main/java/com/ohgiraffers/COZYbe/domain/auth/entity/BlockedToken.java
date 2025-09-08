package com.ohgiraffers.COZYbe.domain.auth.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "UserProfile", timeToLive = 360000)
public class BlockedToken {

    @Id
    private String id;

    private String token;

    @TimeToLive
    private Long ttl;
}
