package com.investmetic.domain.strategy.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfitRateData {
    private final Long strategyId;         // 전략 ID
    private final LocalDate dailyDate;        // 날짜 (YYYY-MM-DD 형식)
    private final Double cumulativeProfitLossRate; // 누적 손익률
}