package com.investmetic.global.security.jwt;

import com.investmetic.domain.user.dto.response.CustomUserDetails;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

/*
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authoriaztion = request.getHeader("Authorization");

        if (authoriaztion == null || !authoriaztion.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            return;
        }
        System.out.println("authorization now");

        String token = authoriaztion.split(" ")[1];

        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = User.builder()
                .userName(username)
                .password("tempassword")
                .role(Role.valueOf(role))
                .build();


        CustomUserDetails customUserDetails = new CustomUserDetails(user, user.getRole());

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println(authToken);
        filterChain.doFilter(request, response);
    }

 */

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken=request.getHeader("access");

        //토큰이 없다면 다음 필터로 넘김
        if(accessToken==null){

            filterChain.doFilter(request,response);

            return;
        }
        //토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{
            jwtUtil.isExpired(accessToken);
        }catch (ExpiredJwtException e){

            PrintWriter writer=response.getWriter();
            writer.println("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){

            PrintWriter writer=response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = User.builder()
                .userName(username)
                .password("tempassword")
                .role(Role.valueOf(role))
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, user.getRole());

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
