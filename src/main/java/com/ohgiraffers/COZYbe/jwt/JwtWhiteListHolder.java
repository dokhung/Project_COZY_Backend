package com.ohgiraffers.COZYbe.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtWhiteListHolder {

    private static final String[] WHITE_LIST = {
            "/api/auth/check-email",
            "/api/auth/register",
            "/api/auth/signup",
            "/api/auth/login",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",

            // Inquiries
            "/api/inquiries/list",

            // Plan
            "/api/pln/list",
            "/api/plan/{id}",
            "/api/plan/by-nickname"
    };

    public String[] getWhiteList(){
        return WHITE_LIST;
    }
}


