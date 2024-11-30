package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final DailyAnalysisScheduler dailyAnalysisScheduler;
    private final StrategySmScoreScheduler strategySmScoreScheduler;
    private final MonthlyAnalysisScheduler monthlyAnalysisScheduler;

    // 매일 자정
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void run() {
        // 새로운 데이터들을 모두 가져옴
        List<DailyAnalysis> pendingAnalyses = dailyAnalysisRepository.findAllByProceedIsFalse();

        pendingAnalyses.forEach(dailyAnalysis -> {
            // 기존에 있는 데이터 가져옴
            Optional<DailyAnalysis> foundDailyAnalysis = dailyAnalysisRepository.findByStrategyAndDailyDateAndProceedIsTrue(
                    dailyAnalysis.getStrategy(),
                    dailyAnalysis.getDailyDate()
            );

            // 이미 데이터가 있을 경우 기존에 있던 데이터는 삭제
            if (foundDailyAnalysis.isPresent()) {
                DailyAnalysis dailyAnalysisResult = foundDailyAnalysis.get();

                dailyAnalysisRepository.delete(dailyAnalysisResult);
            }
        });

        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findEligibleDailyAnalysis();

        dailyAnalyses.forEach(dailyAnalysis -> {
            Long strategyId = dailyAnalysis.getStrategy().getStrategyId();
            LocalDate dailyDate = dailyAnalysis.getDailyDate();

            List<DailyAnalysis> specificDailyAnalyses = dailyAnalysisRepository.findAllByStrategyAndDateAfter(
                    strategyId, dailyDate);

            for (DailyAnalysis specificDailyAnalysis : specificDailyAnalyses) {
                dailyAnalysisScheduler.calculateDailyAnalysis(specificDailyAnalysis);
            }

            Strategy strategy = dailyAnalysis.getStrategy();
            List<DailyAnalysis> strategyDailyAnalyses = dailyAnalysisRepository.findByStrategy(strategy);

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

            if (sumDrawDown == 0 || sumDrawDownPeriod == 0 || totalTradingDays == 0) {
                strategy.setKpRatio(0.0);
            }
            if (Math.sqrt((double) sumDrawDownPeriod / totalTradingDays) == 0) {
                strategy.setKpRatio(0.0);
            }

            strategy.setKpRatio(accumulatedProfitLossRate / (sumDrawDown * -1 * Math.sqrt(
                    (double) sumDrawDownPeriod / totalTradingDays)));

            System.out.println("kpratio + " + accumulatedProfitLossRate / (sumDrawDown * -1 * Math.sqrt(
                    (double) sumDrawDownPeriod / totalTradingDays)));
        });

        strategySmScoreScheduler.calculateSmScores();

        dailyAnalyses.forEach(dailyAnalysis -> {
            List<DailyAnalysis> specificDailyAnalyses = dailyAnalysisRepository.findByStrategyId(
                    dailyAnalysis.getStrategy().getStrategyId());
            monthlyAnalysisScheduler.calculateMonthlyAnalysis(specificDailyAnalyses);
        });

    }
}
