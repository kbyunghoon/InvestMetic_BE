package com.investmetic.global.security.filter;

import com.investmetic.global.security.CustomUserDetails;
import com.investmetic.global.security.service.CustomUserDetailService;
import com.investmetic.global.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final RememberMeServices rememberMeServices;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("access-token");

        if (accessToken == null) {
            Authentication authentication = rememberMeServices.autoLogin(request, response);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String email = authentication.getName();
                String role = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .orElse("ROLE_USER");

                String newAccessToken = jwtUtil.createJwt("access", email, role, 1800000L); // 1시간 유효
                response.setHeader("access-token", "Bearer " + newAccessToken);
                return; // 인증이 성공적으로 처리되었으므로 반환
            }
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = accessToken.replace("Bearer ", "");

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        if (jwtUtil.isExpired(accessToken)) {
            // 토큰이 만료되었으면 응답 처리
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // 응답 상태 코드 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getEmail(accessToken);

        CustomUserDetails user = (CustomUserDetails) customUserDetailService.loadUserByUsername(email);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}