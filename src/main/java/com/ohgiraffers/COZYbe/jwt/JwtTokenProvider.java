package com.ohgiraffers.COZYbe.jwt;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expiration;
    private final Set<String> invalidatedTokens = new HashSet<>(); // 🚀 로그아웃된 토큰 저장

    public JwtTokenProvider(SecretKey jwtHmacKey, @Value("${jwt.expiration}") long expiration) {
        this.secretKey = jwtHmacKey;
        this.expiration = expiration;
    }

    /**
     * 토큰생성
     * <pre>{@code
     * issuer : 발행자, 서버
     * sub : userId
     * audience : 토큰 수신자, 대상 애플리케이션
     * issuedAt : 발행일
     * exp : 만료일
     * content : 추가 가능
     * }</pre>
     *
     * @param userId User UUID;
     * @return JWT Token
     * */
    public String createToken(UUID userId) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("COZY")
                .subject(userId.toString())
                .audience().add("COZY CLIENT").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .content("")
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(UUID userId) {
        long refreshExpiration = expiration * 10;
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("COZY")
                .subject(userId.toString())
                .audience().add("COZY CLIENT").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();

    }


//    @Nullable
    public String decodeUserIdFromJwt(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.error("Token is Empty");
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
//            return null;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        }
    }



    public Long getValidTime() {
        return expiration;
    }
}
