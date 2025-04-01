package com.ohgiraffers.COZYbe.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtWhiteListHolder {

    private static final String[] WHITE_LIST = {
//            "/**",          //모든 경로 (임시)
            "/api/auth/**",  // ✅ 인증 관련 모든 경로 허용
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public String[] getWhiteList(){
        return WHITE_LIST;
    }
}
