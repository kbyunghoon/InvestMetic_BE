package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MonthlyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.statistic.StrategyStatisticsResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//TODO : 추후 조회 성능 개선필요
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StrategyDetailService {
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final StrategyRepository strategyRepository;
    private final SubscriptionRepository subscriptionRepository;

    // 통계 조회
    public StrategyStatisticsResponse getStatistics(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        StrategyStatistics stats = strategy.getStrategyStatistics();
        if (stats == null) {
            throw new BusinessException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        return StrategyStatisticsResponse.from(stats);
    }

    // 일간 분석 조회
    public PageResponseDto<DailyAnalysisResponse> getDailyAnalysis(Long strategyId, Pageable pageable) {
        Page<DailyAnalysisResponse> page = dailyAnalysisRepository.findByStrategyId(strategyId, pageable);

        return new PageResponseDto<>(page);
    }

    // 월간 분석 조회
    public PageResponseDto<MonthlyAnalysisResponse> getMonthlyAnalysis(Long strategyId, Pageable pageable) {
        Page<MonthlyAnalysisResponse> page = monthlyAnalysisRepository.findByStrategyId(strategyId, pageable)
                .map(MonthlyAnalysisResponse::from);

        return new PageResponseDto<>(page);
    }

    // 전략 상세 조회
    public StrategyDetailResponse getStrategyDetail(Long strategyId, Long userId) {
        StrategyDetailResponse strategyDetail = strategyRepository.findStrategyDetail(strategyId);

        // 구독여부 체크
        boolean isSubscribed = subscriptionRepository.existsByStrategyIdAndUserId(strategyId, userId);
        strategyDetail.updateIsSubscribed(isSubscribed);
        return strategyDetail;
    }


    // 전략 분석 조회
    public StrategyAnalysisResponse getStrategyAnalysis(Long strategyId, AnalysisOption option1,
                                                        AnalysisOption option2) {
        validateOption(option1, option2);

        if (option1.equals(option2)) {
            return getSingleStrategyAnalysis(option1, strategyId);
        }
        return dailyAnalysisRepository.findStrategyAnalysis(strategyId, option1, option2);
    }

    private void validateOption(AnalysisOption option1, AnalysisOption option2) {
        if (option1 == null || option2 == null) {
            throw new BusinessException(ErrorCode.ANALYSIS_OPTION_NOT_FOUND);
        }
    }

    // 분석 옵션이 같을때 처리
    private StrategyAnalysisResponse getSingleStrategyAnalysis(AnalysisOption option, Long strategyId) {
        List<String> xAxis = dailyAnalysisRepository.findXAxis(strategyId);
        List<Double> yAxis = dailyAnalysisRepository.findYAxis(strategyId, option);

        return StrategyAnalysisResponse.builder()
                .xAxis(xAxis)
                .yAxis(Map.of(option.name(), yAxis))
                .build();
    }

    public List<DailyAnalysisResponse> getDailyAnalysisExcelData(Long strategyId) {
        return dailyAnalysisRepository.findDailyAnalysisForExcel(strategyId);
    }

    public List<MonthlyAnalysisResponse> getMonthlyAnalysisExcelData(Long strategyId) {
        return monthlyAnalysisRepository.findByStrategyStrategyId(strategyId)
                .stream()
                .map(MonthlyAnalysisResponse::from)
                .toList();
    }

}
