package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.service.StrategyAnalysisService;
import com.investmetic.domain.strategy.service.StrategyRegisterService;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final StrategyAnalysisService strategyAnalysisService;

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

    @PostMapping("/{strategyId}/daily-analysis")
    public ResponseEntity<BaseResponse<Void>> addTraderEntries(
            @PathVariable Long strategyId,
            @RequestBody List<TraderDailyAnalysisRequestDto> dailyAnalysisRequestDtos
    ) {
        strategyAnalysisService.createDailyAnalysis(strategyId, dailyAnalysisRequestDtos);
        return BaseResponse.success();
    }
}
