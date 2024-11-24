package com.investmetic.domain.user.controller;


import com.investmetic.global.security.jwt.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class ReIssueController {

    private final JWTUtil jwtUtil;

    private final RedisUtil redisUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);

        } catch (ExpiredJwtException e) {

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // refresh 토큰인지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //새 토큰 발급
        String newAccess = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L); //30분
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 7 * 24 * 60 * 60 * 1000L); //7일

        response.setHeader("access", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        //기존 refresh token 삭제
        redisUtil.deleteRefreshToken(username);
        redisUtil.saveRefreshToken(username, newRefresh, 7 * 24 * 60 * 60 * 1000L);

        return ResponseEntity.ok(Map.of(
                "accessToken", "Bearer " + newAccess,
                "message", "Token refreshed successfully"
        ));
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
