package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MonthlyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.statistic.StrategyStatisticsResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.service.StrategyDetailService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/{strategyId}")
@Tag(name = "전략 상세페이지 API", description = "전략 상세페이지 관련 API")
public class StrategyDetailController {

    private final StrategyDetailService strategyDetailService;

    @Operation(summary = "전략 통계 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/50c978f6e5a944f2842ad1c48b8f7256' target='_blank'>API 명세서</a>")
    @DeleteMapping("/{reviewId}")
    @GetMapping("/statistics")
    public ResponseEntity<BaseResponse<StrategyStatisticsResponse>> getStrategyStatistics(
            @PathVariable Long strategyId) {
        StrategyStatisticsResponse result = strategyDetailService.getStatistics(strategyId);
        return BaseResponse.success(result);
    }

    @Operation(summary = "전략 일간분석 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/50c978f6e5a944f2842ad1c48b8f7256' target='_blank'>API 명세서</a>")
    @GetMapping("/daily-analysis")
    public ResponseEntity<BaseResponse<PageResponseDto<DailyAnalysisResponse>>> getDailyAnalysis(
            @PathVariable Long strategyId,
            @PageableDefault(size = 5, sort = "dailyDate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<DailyAnalysisResponse> result = strategyDetailService.getDailyAnalysis(strategyId, pageable);
        return BaseResponse.success(result);
    }

    @Operation(summary = "전략 월간분석 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/9c47850bbe2d4dc0823f0ea99690914d' target='_blank'>API 명세서</a>")
    @GetMapping("/monthly-analysis")
    public ResponseEntity<BaseResponse<PageResponseDto<MonthlyAnalysisResponse>>> getMonthlyAnalysis(
            @PathVariable Long strategyId,
            @PageableDefault(size = 5, sort = "monthlyDate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<MonthlyAnalysisResponse> result = strategyDetailService.
                getMonthlyAnalysis(strategyId, pageable);
        return BaseResponse.success(result);
    }

    @Operation(summary = "전략 분석그래프 데이터 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/6affc64db91b4ee6b8d882fa288205bb' target='_blank'>API 명세서</a>")
    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<StrategyDetailResponse>> getStrategyDetail(
            @PathVariable Long strategyId,
            @RequestParam Long userId) {
        StrategyDetailResponse result = strategyDetailService.getStrategyDetail(strategyId, userId);
        return BaseResponse.success(result);
    }

    @Operation(summary = "전략 통계 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/50c978f6e5a944f2842ad1c48b8f7256' target='_blank'>API 명세서</a>")
    @GetMapping("/analysis")
    public ResponseEntity<BaseResponse<StrategyAnalysisResponse>> getStrategyAnalyisis(
            @PathVariable Long strategyId,
            @RequestParam AnalysisOption option1,
            @RequestParam AnalysisOption option2) {
        StrategyAnalysisResponse result = strategyDetailService.getStrategyAnalysis(strategyId, option1, option2);
        return BaseResponse.success(result);
    }
}
