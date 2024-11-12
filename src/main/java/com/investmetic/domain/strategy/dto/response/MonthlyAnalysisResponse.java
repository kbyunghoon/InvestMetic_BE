package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.entity.MonthlyAnalysis;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthlyAnalysisResponse {

    private final String monthlyDate; // YYYY-DD

    private final Long monthlyAveragePrincipal; //  월평균 원금

    private final Long depositsWithdrawals; // 입출금

    private final Long monthlyProfitLoss; // 월 손익

    private final Double monthlyProfitLossRate; // 월 손익률

    private final Long cumulativeProfitLoss; // 누적 손익

    private final Double cumulativeProfitLossRate; // 누적 손익률


    public static MonthlyAnalysisResponse from(MonthlyAnalysis monthlyAnalysis) {
        return new MonthlyAnalysisResponse(
                monthlyAnalysis.getMonthlyDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                monthlyAnalysis.getMonthlyAveragePrincipal(),
                monthlyAnalysis.getDepositsWithdrawals(),
                monthlyAnalysis.getMonthlyProfitLoss(),
                monthlyAnalysis.getMonthlyProfitLossRate(),
                monthlyAnalysis.getCumulativeProfitLoss(),
                monthlyAnalysis.getCumulativeProfitLossRate()
        );
    }
}
