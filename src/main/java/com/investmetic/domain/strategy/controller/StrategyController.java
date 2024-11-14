package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.service.StrategyRegisterService;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
@Tag(name = "전략 API", description = "전략 관련 API")
public class StrategyController {
    private final StrategyRegisterService strategyRegisterService;

    @PostMapping("/register")
    @Operation(summary = "전략 등록", description = "<a href='https://field-sting-eff.notion.site/9dbecd9a350942a6aa38204329a1c186?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> registerStrategy(
            @RequestBody StrategyRegisterRequestDto requestDto) {
        return BaseResponse.success(SuccessCode.CREATED, strategyRegisterService.registerStrategy(requestDto));
    }

    @GetMapping("/register")
    @Operation(summary = "전략 등록 페이지 진입 시 요청", description = "<a href='https://field-sting-eff.notion.site/f1e0b17145a74ace9b5cfec0e6e408ed?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {
        return BaseResponse.success(strategyRegisterService.loadStrategyRegistrationInfo());
    }
}
