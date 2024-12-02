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
    private final StrategyCalculatorScheduler strategyCalculatorScheduler;
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

            strategyCalculatorScheduler.calculateKpRatio(strategyDailyAnalyses, strategy);
        });

        strategyCalculatorScheduler.calculateSmScores();

        dailyAnalyses.forEach(dailyAnalysis -> {
            List<DailyAnalysis> specificDailyAnalyses = dailyAnalysisRepository.findByStrategyId(
                    dailyAnalysis.getStrategy().getStrategyId());
            monthlyAnalysisScheduler.calculateMonthlyAnalysis(specificDailyAnalyses);
        });
    }
}
