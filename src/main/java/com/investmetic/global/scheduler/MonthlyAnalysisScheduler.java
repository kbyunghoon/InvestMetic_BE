package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.MonthlyAnalysis;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.global.util.RoundUtil;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthlyAnalysisScheduler {
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;

    public void calculateMonthlyAnalysis(List<DailyAnalysis> dailyAnalyses) {
        Map<YearMonth, List<DailyAnalysis>> groupedByMonth = dailyAnalyses.stream()
                .collect(Collectors.groupingBy(daily -> YearMonth.from(daily.getDailyDate())));

        groupedByMonth.forEach((month, dailyAnalysisList) -> {
            DailyAnalysis lastDayAnalysis = dailyAnalysisList.get(dailyAnalysisList.size() - 1);

            Long monthlyPrincipal = lastDayAnalysis.getPrincipal();

            Long monthlyTransaction = dailyAnalysisList.stream()
                    .mapToLong(DailyAnalysis::getTransaction)
                    .sum();

            Long monthlyProfitLoss = dailyAnalysisList.stream()
                    .mapToLong(DailyAnalysis::getDailyProfitLoss)
                    .sum();

            Double monthlyProfitLossRate = dailyAnalysisList.stream()
                    .mapToDouble(DailyAnalysis::getDailyProfitLossRate)
                    .average()
                    .orElse(0.0);

            Long monthlyCumulativeProfitLoss = lastDayAnalysis.getCumulativeProfitLoss();

            Double monthlyCumulativeProfitLossRate = lastDayAnalysis.getCumulativeProfitLossRate();

            LocalDate monthlyDate = month.atEndOfMonth();

            Optional<MonthlyAnalysis> existingMonthlyAnalysis = monthlyAnalysisRepository.findByStrategyAndMonthlyDate(
                    dailyAnalysisList.get(0).getStrategy(), monthlyDate);

            if (existingMonthlyAnalysis.isPresent()) {
                // 이미 존재하면 업데이트
                MonthlyAnalysis updateMonthlyAnalysis = existingMonthlyAnalysis.get();
                updateMonthlyAnalysis.setMonthlyAnalysisData(monthlyPrincipal, monthlyTransaction, monthlyProfitLoss,
                        RoundUtil.roundToFifth(monthlyProfitLossRate), monthlyCumulativeProfitLoss,
                        RoundUtil.roundToFifth(monthlyCumulativeProfitLossRate));
            } else {
                // 존재하지 않으면 새로 생성 및 저장
                MonthlyAnalysis newMonthlyAnalysis = MonthlyAnalysis.builder()
                        .strategy(dailyAnalysisList.get(0).getStrategy())
                        .monthlyDate(month.atEndOfMonth())
                        .monthlyAveragePrincipal(monthlyPrincipal)
                        .depositsWithdrawals(monthlyTransaction)
                        .monthlyProfitLoss(monthlyProfitLoss)
                        .monthlyProfitLossRate(RoundUtil.roundToFifth(monthlyProfitLossRate))
                        .cumulativeProfitLoss(monthlyCumulativeProfitLoss)
                        .cumulativeProfitLossRate(RoundUtil.roundToFifth(monthlyCumulativeProfitLossRate))
                        .build();

                monthlyAnalysisRepository.save(newMonthlyAnalysis);
            }
        });
    }

}
