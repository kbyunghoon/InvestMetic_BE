package com.investmetic.global.security.service;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReIssueService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${jwt.expiration.access}")
    private Long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private Long refreshExpiration;

    @Transactional
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null || refresh.isEmpty()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 리프레시 토큰인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUsername(refresh); // 이메일
        String role = jwtUtil.getRole(refresh);

        // 기존 리프레시 토큰 삭제
        redisUtil.deleteRefreshToken(refresh);

        // 새 토큰 발급
        String newAccess = jwtUtil.createJwt("access", username, role, accessExpiration); // 30분
        String newRefresh = jwtUtil.createJwt("refresh", username, role, refreshExpiration); // 7일


        // 새로운 리프레시 토큰을 쿠키에 추가
        Cookie newRefreshCookie = createCookie("refresh", newRefresh, refreshExpiration);
        response.addCookie(newRefreshCookie);

        redisUtil.deleteRefreshToken(username);
        redisUtil.saveRefreshToken(username, newRefresh, refreshExpiration);

        // 응답 헤더에 새로운 access 토큰 추가
        response.setHeader("access", "Bearer " + newAccess);
    }

    private Cookie createCookie(String key, String value, Long maxAgeMillis) {
        int maxAgeSec = (int) (maxAgeMillis / 1000);

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAgeSec);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); // HTTPS 사용 시 활성화
        //cookie.setPath("/"); // 필요에 따라 경로 설정
        return cookie;
    }
}