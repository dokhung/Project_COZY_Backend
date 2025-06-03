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
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

//    @Bean
//    public JwtTokenProvider jwtTokenProvider(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.expiration}") long expiration) {
//        return new JwtTokenProvider(secret, expiration);
//    }
}
