package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
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
            Strategy strategy = findStrategyById(strategyId);

            // proceed가 false이고 dailyDate가 같은 값을 가져옴
            Optional<DailyAnalysis> existsDailyData = dailyAnalysisRepository.findByStrategyAndDailyDateAndProceedIsFalse(
                    strategy,
                    analysisRequest.getDate());

            if (existsDailyData.isPresent()) {
                DailyAnalysis updatedDailyAnalysis = existsDailyData.get().toBuilder()
                        .transaction(analysisRequest.getTransaction())
                        .dailyProfitLoss(analysisRequest.getDailyProfitLoss())
                        .build();

                dailyAnalysisRepository.save(updatedDailyAnalysis);
            } else {
                DailyAnalysis dailyAnalysis = DailyAnalysis.builder()
                        .strategy(strategy)
                        .dailyDate(analysisRequest.getDate())
                        .transaction(analysisRequest.getTransaction())
                        .dailyProfitLoss(analysisRequest.getDailyProfitLoss())
                        .proceed(false)
                        .build();

                dailyAnalysisRepository.save(dailyAnalysis);
            }
        }
    }

    @Transactional
    public void deleteStrategyAllDailyAnalysis(Long strategyId) {
        Strategy strategy = findStrategyById(strategyId);

        // TODO : 유저 권한 확인 로직 추가 예정
        strategy.resetStrategyDailyAnalysis();

        dailyAnalysisRepository.deleteAllByStrategy(strategy);
    }

    private Strategy findStrategyById(Long strategyId) {
        return strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
    }
}
