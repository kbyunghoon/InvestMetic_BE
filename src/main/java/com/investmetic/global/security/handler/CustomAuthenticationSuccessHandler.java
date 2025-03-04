package com.investmetic.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.global.util.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RememberMeServices rememberMeServices;

    @Value("${jwt.expiration.access}")
    private Long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private Long refreshExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = authentication.getName();
        Boolean rememberMe = (Boolean) authentication.getDetails();

        // "로그인 유지하기" 옵션이 선택된 경우에만 RememberMeServices 호출
        if (Boolean.TRUE.equals(rememberMe)) {
            rememberMeServices.loginSuccess(request, response, authentication);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", email, role, accessExpiration);
        String refresh = jwtUtil.createJwt("refresh", email, role, refreshExpiration);

        redisUtil.saveRefreshToken(email, refresh, refreshExpiration);
//        rememberMeServices.loginSuccess(request, response, authentication);

        // 응답 헤더와 JSON 설정
        response.setHeader("access-token", "Bearer " + access);
        response.addCookie(createCookie("refresh-token", refresh, refreshExpiration));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = objectMapper.writeValueAsString(
                new SuccessResponse(true, "로그인이 완료되었습니다.")
        );
        response.getWriter().write(responseBody);
        response.setStatus(HttpStatus.OK.value());
    }

    private Cookie createCookie(String key, String value, Long maxAgeMillis) {

        int maxAgeSec = (int) (maxAgeMillis / 1000);
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAgeSec);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void deleteOldRefreshCookie(HttpServletResponse response) {
        Cookie oldRefreshCookie = new Cookie("refresh", null);
        oldRefreshCookie.setPath("/");
        oldRefreshCookie.setMaxAge(0); // 쿠키 삭제
        oldRefreshCookie.setHttpOnly(true);
        //oldRefreshCookie.setSecure(true);
        //oldRefreshCookie.setSameSite("Strict");
        response.addCookie(oldRefreshCookie);
    }

    private record SuccessResponse(boolean isSuccess, String message) {
    }
}