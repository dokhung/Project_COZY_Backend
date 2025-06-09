package com.ohgiraffers.COZYbe.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtWhiteListHolder {

    private static final String[] WHITE_LIST = {
//            "/**",          //모든 경로 (임시)
            "/api/auth/check-email",
            "/api/auth/register",
            "/api/auth/signup",
            "/api/auth/login",
//            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public String[] getWhiteList(){
        return WHITE_LIST;
    }
}
