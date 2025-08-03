package com.ohgiraffers.COZYbe.config;

import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class AppConfig {

    @Bean
    public SecretKey jwtHmacKey(@Value("${jwt.secret}") String secret) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());        //HmacSHA512 로 적용됨
        return secretKey;
    }
}
