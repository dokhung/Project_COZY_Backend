package com.ohgiraffers.COZYbe.jwt;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expiration;
    private final Set<String> invalidatedTokens = new HashSet<>(); // 🚀 로그아웃된 토큰 저장

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
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
                .issuer("COZY")
                .subject(userId.toString())
                .audience().add("COZY CLIENT").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .content("")
                .signWith(key)
                .compact();
    }

    // ✅ 토큰에서 userId 추출
//    @Nullable
    public String decodeUserIdFromJwt(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.error("Token is Empty");
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
//            System.out.println("❌ [JWT] 토큰이 비어있음");
//            return null;
        }

        try {
            Claims claims = Jwts.parser()           // ✅ parserBuilder → parser
                    .verifyWith((SecretKey) key)               // ✅ setSigningKey → verifyWith
                    .build()
                    .parseSignedClaims(token)      // ✅ parseClaimsJws → parseSignedClaims
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
//            System.out.println("❌ [JWT 파싱 오류] " + e.getMessage());
//            return null;
            log.error(e.getMessage());
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        }
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        if (invalidatedTokens.contains(token)) {
//            System.out.println("❌ [JWT 프로바이더] 무효화된 토큰입니다.");
            log.info("무효화된 토큰");
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.JwtException e) {
//            System.out.println("❌ [JWT 프로바이더] JWT 검증 실패: " + e.getMessage());
            log.error(e.getMessage());
            return false;
        }
    }

    // ✅ 로그아웃된 토큰 무효화
    public void invalidateToken(String token) {
//        System.out.println("🚀 [JWT 프로바이더] 토큰 무효화 처리: " + token);
        log.info("토큰 무효화 처리 : {}", token);
        invalidatedTokens.add(token);
    }

    // ✅ 로그아웃된 토큰인지 확인
    public boolean isTokenValid(String token) {
        return !invalidatedTokens.contains(token);
    }
}
