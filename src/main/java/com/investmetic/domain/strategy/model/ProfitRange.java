package com.investmetic.domain.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfitRange {
    UNDER_10_PERCENT("10% 이하", 0.0, 10.0),
    BETWEEN_10_AND_20("10%~20%", 10.0, 20.0),
    BETWEEN_20_AND_30("20%~30%", 20.0, 30.0),
    OVER_30_PERCENT("30% 이상", 30.0, Double.MAX_VALUE);

    private final String description;
    private final double minRate;     // 최소 수익률
    private final double maxRate;     // 최대 수익률

}