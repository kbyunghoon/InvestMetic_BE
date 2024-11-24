package com.investmetic.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.global.security.jwt.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 7 * 24 * 60 * 60 * 1000L);

        redisUtil.saveRefreshToken(username, refresh, 7 * 24 * 60 * 60L);

        // 응답 헤더와 JSON 설정
        response.setHeader("access", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = objectMapper.writeValueAsString(
                new SuccessResponse(true, "로그인이 완료되었습니다.")
        );
        response.getWriter().write(responseBody);
        response.setStatus(HttpStatus.OK.value());
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        return cookie;
    }

    private record SuccessResponse(boolean isSuccess, String message) {
    }
}