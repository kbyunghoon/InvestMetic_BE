package com.investmetic.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.global.util.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private final RedisUtil redisUtil;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);

            String email = requestBody.get("email");
            String password = requestBody.get("password");

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}