package com.ohgiraffers.COZYbe.domain.user.service;

import org.springframework.stereotype.Service;

import java.util.Map;

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
