package com.ohgiraffers.COZYbe.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expiration;
    private final Set<String> invalidatedTokens = new HashSet<>(); // 🚀 로그아웃된 토큰 저장

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // ✅ JWT 생성
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰에서 사용자 이름(이메일) 추출
    public String getUsernameFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("❌ [JWT] 토큰이 비어있음");
            return null;
        }

        try {
            Claims claims = Jwts.parser()           // ✅ parserBuilder → parser
                    .verifyWith((SecretKey) key)               // ✅ setSigningKey → verifyWith
                    .build()
                    .parseSignedClaims(token)      // ✅ parseClaimsJws → parseSignedClaims
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("❌ [JWT 파싱 오류] " + e.getMessage());
            return null;
        }
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            if (invalidatedTokens.contains(token)) {
                System.out.println("❌ [JWT 프로바이더] 무효화된 토큰입니다.");
                return false;
            }
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ [JWT 프로바이더] JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    // ✅ 로그아웃된 토큰 무효화
    public void invalidateToken(String token) {
        System.out.println("🚀 [JWT 프로바이더] 토큰 무효화 처리: " + token);
        invalidatedTokens.add(token);
    }

    // ✅ 로그아웃된 토큰인지 확인
    public boolean isTokenValid(String token) {
        return !invalidatedTokens.contains(token);
    }
}
