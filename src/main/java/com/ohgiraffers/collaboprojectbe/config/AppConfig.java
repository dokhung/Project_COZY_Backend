package com.ohgiraffers.collaboprojectbe.config;

import com.ohgiraffers.collaboprojectbe.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return new JwtTokenProvider();
    }
}
