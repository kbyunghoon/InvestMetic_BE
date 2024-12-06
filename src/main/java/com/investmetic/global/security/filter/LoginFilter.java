package com.investmetic.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.domain.user.dto.request.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String email = null;
        String password = null;

        try {
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            email = loginRequestDto.getEmail();
            password = loginRequestDto.getPassword();

            //Spring Security에서 인증 정보를 담는 객체
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);
            authToken.setDetails(loginRequestDto.getRemember()); //인증 요청에 필요한 추가 정보를 담을 수 있도록 설계된 메서드입니다.

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}