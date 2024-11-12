package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MonthlyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.statistic.StrategyStatisticsResponse;
import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StrategyDetailService {
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;

    // 통계 조회
    public StrategyStatisticsResponse getStatistics(Long strategyId) {

        StrategyStatistics stats = strategyStatisticsRepository.findByStrategy(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        return StrategyStatisticsResponse.from(stats);
    }

    // 일간 분석 조회
    public PageResponseDto<DailyAnalysisResponse> getDailyAnalysis(Long strategyId, Pageable pageable) {
        Page<DailyAnalysisResponse> page = dailyAnalysisRepository.findByStrategyId(strategyId, pageable)
                .map(DailyAnalysisResponse::from);

        return new PageResponseDto<>(page);
    }

    // 월간 분석 조회
    public PageResponseDto<MonthlyAnalysisResponse> getMonthlyAnalysis(Long strategyId, Pageable pageable) {
        Page<MonthlyAnalysisResponse> page = monthlyAnalysisRepository.findByStrategyId(strategyId, pageable)
                .map(MonthlyAnalysisResponse::from);

        return new PageResponseDto<>(page);
    }

}
