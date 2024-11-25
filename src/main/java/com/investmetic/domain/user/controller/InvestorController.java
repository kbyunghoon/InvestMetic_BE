package com.investmetic.domain.user.controller;

import com.investmetic.global.security.jwt.JWTUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestorController {

    private final JWTUtil jwtUtil;

    public InvestorController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/investor")
    public String investorEndpoint(@RequestHeader("Authorization") String authorizationHeader) {
        // Bearer 토큰에서 실제 토큰 값 추출
        String token = authorizationHeader.replace("Bearer ", "");

        // JWT에서 역할(Role) 추출
        String role = jwtUtil.getRole(token);

        // 디버깅용 출력
        System.out.println("Access Token Role: " + role);

        // 역할을 리턴하거나 다른 처리
        return "Welcome, Investor!";
    }
}