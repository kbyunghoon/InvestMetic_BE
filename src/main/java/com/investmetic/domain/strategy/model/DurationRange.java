package com.investmetic.domain.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DurationRange {
    ONE_YEAR_OR_LESS("1년 이하", 0, 365),        // 1년 이하
    ONE_TO_TWO_YEARS("1년~2년", 366, 730),      // 1년 ~ 2년
    TWO_TO_THREE_YEARS("2년~3년", 731, 1095),   // 2년 ~ 3년
    THREE_YEARS_OR_MORE("3년 이상", 1096, Integer.MAX_VALUE); // 3년 이상

    private final String description;
    private final int minDays;     // 최소 기간 (월)
    private final int maxDays;     // 최대 기간 (월)
}