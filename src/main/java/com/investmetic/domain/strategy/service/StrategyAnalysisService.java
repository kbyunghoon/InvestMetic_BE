package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyAnalysisService {
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final StrategyRepository strategyRepository;

    @Transactional
    public void createDailyAnalysis(Long strategyId, List<TraderDailyAnalysisRequestDto> analysisRequests) {
        for (TraderDailyAnalysisRequestDto analysisRequest : analysisRequests) {
            Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() -> new BusinessException(
                    ErrorCode.STRATEGY_NOT_FOUND));

            boolean existsDailyData = dailyAnalysisRepository.existsByStrategyAndDailyDate(strategy,
                    analysisRequest.getDate());

            if (existsDailyData) {
                throw new BusinessException(ErrorCode.DAILY_ANALYSIS_ALREADY_EXISTS);
            }

            DailyAnalysis dailyAnalysis = DailyAnalysis.builder()
                    .strategy(strategy)
                    .dailyDate(analysisRequest.getDate())
                    .transaction(analysisRequest.getTransaction())
                    .dailyProfitLoss(analysisRequest.getDailyProfitLoss())
                    .build();

            dailyAnalysisRepository.save(dailyAnalysis);
        }
    }
}
