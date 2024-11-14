package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyController {
    private final StrategyService strategyService;

    @GetMapping("/register")
    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {
        return strategyService.loadStrategyRegistrationInfo();
    }
}
