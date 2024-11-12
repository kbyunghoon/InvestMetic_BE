package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DailyAnalysisResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate dailyDate;
    private final long principal;                    // 원금
    private final long transaction;                  // 입출금
    private final long dailyProfitLoss;              // 일손익
    private final double dailyProfitLossRate;        // 일 수익률
    private final double cumulativeProfitLoss;       // 누적 손익
    private final double cumulativeProfitLossRate;   // 누적 수익률

    public static DailyAnalysisResponse from(DailyAnalysis dailyAnalysis) {
        return new DailyAnalysisResponse(
                dailyAnalysis.getDailyDate(),
                dailyAnalysis.getPrincipal(),
                dailyAnalysis.getTransaction(),
                dailyAnalysis.getDailyProfitLoss(),
                dailyAnalysis.getDailyProfitLossRate(),
                dailyAnalysis.getCumulativeProfitLoss(),
                dailyAnalysis.getCumulativeProfitLossRate()
        );
    }
}
