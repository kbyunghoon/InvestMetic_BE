package com.investmetic.global.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String getEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // 서명 검증 키 설정
                .build()
                .parseClaimsJws(token)     // 토큰 검증 및 파싱
                .getBody()                 // Payload (Claims) 반환
                .get("email", String.class); // "username" 필드 추출
    }

    public String getRole(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // 서명 검증 키 설정
                .build()
                .parseClaimsJws(token)     // 토큰 검증 및 파싱
                .getBody()                 // Claims(Payload) 반환
                .get("role", String.class); // "role" 필드 추출
    }

    public Boolean isExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 서명 검증 키 설정
                    .build()
                    .parseClaimsJws(token)     // 토큰 검증 및 파싱
                    .getBody()                 // Claims(Payload) 반환
                    .getExpiration()           // 만료 시간 확인
                    .before(new Date());       // 현재 시간과 비교
        } catch (JwtException e) {
            return true;  // 예외 발생 시 만료된 것으로 간주
        }
    }


    public String createJwt(String category, String email, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)  // 토큰 종류
                .claim("email", email) // 사용자 이메일 추가
                .claim("role", role)         // 사용자 역할 추가
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey) // 비밀키와 알고리즘 설정
                .compact(); // JWT 생성
    }

    public String getCategory(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Secret key 설정
                .build()
                .parseClaimsJws(token) // JWT 토큰을 파싱
                .getBody() // Payload 추출
                .get("category", String.class); // Payload에서 "category" 필드 추출
    }
}
