package com.investmetic.domain.strategy.dto.response.statistic;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class AggregatedStatisticsDto {
    private Long balance;                    // 최대 잔고
    private Double profitLossStdDev;            // 수익률 표준편차
    private Integer profitableDays;             // 총 이익 일수
    private Integer lossDays;                   // 총 손실 일수
    private Long maxDrawdown;                   // 최대 자본 인하 금액
    private Long cumulativeProfitLoss;          // 누적 손익 금액
    private Double maxCumulativeProfitRate;     // 최대 누적 수익률
    private LocalDate startDate;                // 시작일
    private LocalDate endDate;                  // 종료일
    private Long cumulativeTransactionAmount;   // 누적 입출금액
    private Long maxDailyProfit;                // 최대 일간 이익
    private Long maxDailyLoss;                  // 최대 일간 손실
    private Double winRate;                     // 승률
    private Double maxDailyProfitRate;          // 최대 일간 이익률
    private Double maxDailyLossRate;            // 최대 일간 손실률

}