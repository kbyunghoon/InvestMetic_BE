package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.accountverification.dto.response.AccountImagesResponseDto;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MonthlyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.statistic.StrategyStatisticsResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.service.StrategyDetailService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.util.exceldownload.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    private final ExcelUtils excelUtils;
    private static final String DAILY_ANALYSIS_EXCEL_NAME = "daily_analysis";
    private static final String MONTHLY_ANALYSIS_EXCEL_NAME = "monthly_analysis";

    @Operation(summary = "전략 통계 조회(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/50c978f6e5a944f2842ad1c48b8f7256' target='_blank'>API 명세서</a>")
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

    @Operation(summary = "전략 일간분석 엑셀다운(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/42416d40378940648f4798070a6ac5ca' target='_blank'>API 명세서</a>")
    @GetMapping("/daily-analysis/download")
    public void dailyAnalysisExcelDownload(
            @PathVariable Long strategyId,
            HttpServletResponse response) {
        // 1. HTTP 응답 객체를 ExcelUtils에 전달하여 초기화
        excelUtils.connect(response);

        // 시트 생성 및 데이터 추가
        List<DailyAnalysisResponse> dailyAnalysisData = strategyDetailService.getDailyAnalysisExcelData(strategyId);
        excelUtils.draw(DailyAnalysisResponse.class, dailyAnalysisData);

        // 엑셀 파일 다운로드
        excelUtils.download(DAILY_ANALYSIS_EXCEL_NAME);
    }

    @Operation(summary = "전략 월간분석 엑셀다운(전략 상세페이지) ",
            description = "<a href='https://www.notion.so/7ba6f427a5594eefb3e9bd103e6ccc31' target='_blank'>API 명세서</a>")
    @GetMapping("/monthly-analysis/download")
    public void monthlyAnalysisExcelDownload(
            @PathVariable Long strategyId,
            HttpServletResponse response) {
        // 1. HTTP 응답 객체를 ExcelUtils에 전달하여 초기화
        excelUtils.connect(response);

        // 시트 생성 및 데이터 추가
        List<MonthlyAnalysisResponse> monthlyAnalysisExcelData = strategyDetailService.getMonthlyAnalysisExcelData(
                strategyId);
        excelUtils.draw(MonthlyAnalysisResponse.class, monthlyAnalysisExcelData);

        // 엑셀 파일 다운로드
        excelUtils.download(MONTHLY_ANALYSIS_EXCEL_NAME);
    }


    @Operation(summary = "전략 실계좌 이미지 목록조회 (전략 상세페이지) ",
            description = "<a href='https://www.notion.so/81d16fa5d985466899d4284e8ed04098' target='_blank'>API 명세서</a>")
    @GetMapping("/account-images")
    public ResponseEntity<BaseResponse<PageResponseDto<AccountImagesResponseDto>>> getStrategyAccountImages(
            @PathVariable Long strategyId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return BaseResponse.success(strategyDetailService.getAccountImages(strategyId, pageable));
    }
}
