package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StrategySmScoreScheduler {
    private final StrategyRepository strategyRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;

    @Transactional
    public void calculateSmScores() {
        List<Strategy> strategiesList = strategyRepository.findAll();

        // 1. 각 전략의 최신 KP Ratio를 가져오고 설정
        setLatestKpRatios(strategiesList);

        // 2. KP Ratio의 평균과 표준 편차 계산
        double[] stats = calculateStatistics(strategiesList);
        double mean = stats[0];
        double standardDeviation = stats[1];

        // 3. Z-Score와 SM Score 계산
        calculateZScoresAndSmScores(strategiesList, mean, standardDeviation);

        // 4. 전략 저장
        strategyRepository.saveAll(strategiesList);
    }

    private void setLatestKpRatios(List<Strategy> strategiesList) {
        strategiesList.forEach(strategy -> {
            Double kpRatio = dailyAnalysisRepository.findLatestKpRatioByStrategyId(strategy.getStrategyId())
                    .orElse(0.0);
            strategy.setKpRatio(kpRatio);
        });
    }

    private double[] calculateStatistics(List<Strategy> strategiesList) {
        List<Double> kpRatios = strategiesList.stream()
                .map(Strategy::getKpRatio)
                .toList();

        double mean = kpRatios.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double standardDeviation = Math.sqrt(kpRatios.stream()
                .mapToDouble(kp -> Math.pow(kp - mean, 2))
                .average()
                .orElse(0.0));

        return new double[]{mean, standardDeviation};
    }

    private void calculateZScoresAndSmScores(List<Strategy> strategiesList, double mean, double standardDeviation) {
        NormalDistribution standardNormal = new NormalDistribution(0, 1);

        strategiesList.forEach(strategy -> {
            if (standardDeviation == 0.0) {
                strategy.setSmScore(100.0);
            } else {
                double zScore = (strategy.getKpRatio() - mean) / standardDeviation;
                double cdfValue = standardNormal.cumulativeProbability(zScore);
                strategy.setSmScore(cdfValue * 100);
            }
        });
    }
}
