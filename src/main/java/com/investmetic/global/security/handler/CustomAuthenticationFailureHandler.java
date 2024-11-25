package com.investmetic.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ErrorCode errorCode = ErrorCode.LOGIN_FAILED;

        if (exception instanceof BadCredentialsException) {
            errorCode = ErrorCode.LOGIN_FAILED;
        }

        // 응답 설정
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json; charset=UTF-8"); // Content-Type에 UTF-8 설정

        mapper.writeValue(response.getWriter(), Map.of(

                "code", errorCode.getCode(),
                "message", errorCode.getMessage(),
                "isSuccess", false
        ));

    }
}
