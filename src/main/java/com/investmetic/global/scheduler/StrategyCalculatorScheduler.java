package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.util.RoundUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StrategyCalculatorScheduler {
    private final StrategyRepository strategyRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;

    @Transactional
    public void calculateKpRatio(List<DailyAnalysis> strategyDailyAnalyses, Strategy strategy) {
        Double highProfitLossRate = 0.0;
        Double minDrawDown = 0.0;
        Double sumDrawDown = 0.0;
        Long sumDrawDownPeriod = 0L;

        for (DailyAnalysis strategyDailyAnalysis : strategyDailyAnalyses) {
            Double currentProfitLossRate = strategyDailyAnalysis.getCumulativeProfitLossRate();

            if (highProfitLossRate > currentProfitLossRate) {
                // 손익률 인하되는 시점
                sumDrawDownPeriod++;
                if (currentProfitLossRate - highProfitLossRate < minDrawDown) {
                    // DD 갱신
                    minDrawDown = currentProfitLossRate - highProfitLossRate;
                }
            } else {
                highProfitLossRate = currentProfitLossRate;
                sumDrawDown += minDrawDown;
                minDrawDown = 0.0;
            }
        }

        long totalTradingDays = strategyDailyAnalyses.size();

        double accumulatedProfitLossRate = strategyDailyAnalyses.get(strategyDailyAnalyses.size() - 1)
                .getCumulativeProfitLossRate();

        if (sumDrawDown == 0 || sumDrawDownPeriod == 0) {
            strategy.setKpRatio(0.0);
            return;
        }
        if (Math.sqrt((double) sumDrawDownPeriod / totalTradingDays) == 0) {
            strategy.setKpRatio(0.0);
            return;
        }

        Double kpRatio = accumulatedProfitLossRate / (sumDrawDown * -1 * Math.sqrt(
                (double) sumDrawDownPeriod / totalTradingDays));

        strategy.setKpRatio(RoundUtil.roundToFifth(kpRatio));
    }

    @Transactional
    public void calculateSmScores() {
        List<Strategy> strategiesList = strategyRepository.findAll();

        // 2. KP Ratio의 평균과 표준 편차 계산
        double[] stats = calculateStatistics(strategiesList);
        double mean = stats[0];
        double standardDeviation = stats[1];

        // 3. Z-Score와 SM Score 계산
        calculateZScoresAndSmScores(strategiesList, mean, standardDeviation);

        // 4. 전략 저장
        strategyRepository.saveAll(strategiesList);
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
                strategy.setSmScore(RoundUtil.roundToFifth(cdfValue * 100));
            }
        });
    }
}
