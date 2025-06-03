package com.ohgiraffers.COZYbe.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;

@Configuration
public class JwtDecoderConfig {

    private final SecretKey secretKey;

//    public JwtDecoderConfig(@Value("${jwt.secret}") String secret) {
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
//    }

    public JwtDecoderConfig(SecretKey jwtHmacKey){
        this.secretKey = jwtHmacKey;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .build();
    }
}
