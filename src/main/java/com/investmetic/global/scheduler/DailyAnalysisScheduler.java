package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyAnalysisScheduler {

    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final Scheduler scheduler;

    // 매일 자정
    @Scheduled(cron = "40 * * * * *")
    public void run() {
        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findEligibleDailyAnalysis();
        dailyAnalyses.forEach(dailyAnalysis -> {
            Long strategyId = dailyAnalysis.getStrategy().getStrategyId();
            LocalDate dailyDate = dailyAnalysis.getDailyDate();
            List<DailyAnalysis> specificDailyAnalyses = dailyAnalysisRepository.findAllByStrategyAndDateAfter(
                    strategyId, dailyDate);
            for (DailyAnalysis specificDailyAnalysis : specificDailyAnalyses) {
                System.out.println(specificDailyAnalysis.getDailyDate());
                scheduler.test(specificDailyAnalysis);
            }
        });
    }
}
