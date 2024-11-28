package com.investmetic.domain.user.service;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
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

        if (jwtUtil.isExpired(refresh)) {
            // 만약 토큰이 만료되었다면, 비즈니스 예외를 던짐
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 리프레시 토큰인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUsername(refresh); // 사용자 이메일
        String key = "REFRESH_TOKEN:" + username; // Redis 키 생성

        // Redis에 저장되어 있는지 확인
        boolean isExist = redisUtil.existData(key);
        if (!isExist) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 로그아웃 진행: Redis에서 리프레시 토큰 삭제
        redisUtil.deleteRefreshToken(username);

        // 리프레시 토큰 쿠키 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); // HTTPS 사용 시 활성화
        //cookie.setSameSite("Strict"); // CSRF 방지를 위해 SameSite 설정

        response.addCookie(cookie);
        // 응답 헤더에 Access Token 제거 또는 무효화 (선택 사항)
        response.setHeader("access", null);
    }
}