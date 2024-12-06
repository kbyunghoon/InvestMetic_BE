package com.investmetic.domain.strategy.dto.response.statistic;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 손익률 관련정보
 */
@Getter
@Builder
@RequiredArgsConstructor
public class ProfitLossDto {
    private final Long cumulativeProfitAmount;   // 누적 수익 금액
    private final Double cumulativeProfitRate;   // 누적 수익률
    private final Long maxCumulativeProfitAmount; // 최대 누적 수익 금액
    private final Double maxCumulativeProfitRate; // 최대 누적 수익률
    private final Long averageProfitLossAmount;  // 평균 손익 금액
    private final Double averageProfitLossRate;  // 평균 손익률
    private final Long maxDailyProfitAmount;     // 최대 일 수익 금액
    private final Double maxDailyProfitRate;     // 최대 일 수익률
    private final Long maxDailyLossAmount;       // 최대 일 손실 금액
    private final Double maxDailyLossRate;       // 최대 일 손실률
    private final Double roa;                    // 자산 수익률 (ROA)
    private final Double profitFactor;           // Profit Factor

    public static ProfitLossDto from(StrategyStatistics stats) {
        return ProfitLossDto.builder()
                .cumulativeProfitAmount(stats.getCumulativeProfitAmount())
                .cumulativeProfitRate(stats.getCumulativeProfitRate())
                .maxCumulativeProfitAmount(stats.getMaxCumulativeProfitAmount())
                .maxCumulativeProfitRate(stats.getMaxCumulativeProfitRate())
                .averageProfitLossAmount(stats.getAverageProfitLossAmount())
                .averageProfitLossRate(stats.getAverageProfitLossRate())
                .maxDailyProfitAmount(stats.getMaxDailyProfitAmount())
                .maxDailyProfitRate(stats.getMaxDailyProfitRate())
                .maxDailyLossAmount(stats.getMaxDailyLossAmount())
                .maxDailyLossRate(stats.getMaxDailyLossRate())
                .roa(stats.getRoa())
                .profitFactor(stats.getProfitFactor())
                .build();
    }
}
