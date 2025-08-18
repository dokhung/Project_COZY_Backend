package com.ohgiraffers.COZYbe.domain.auth.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 임시로 만든 블락리스트
 * Redis 구현 예정
 * */
@Service
public class BlocklistService {
    private Map<String, Long> blocklist;

    public boolean exists(String id) {
        return blocklist.containsKey(id);
    }

    public void store(String jti, long ttl) {
        blocklist.put(jti,ttl);
    }
}
