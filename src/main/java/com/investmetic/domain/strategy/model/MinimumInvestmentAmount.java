package com.investmetic.domain.strategy.model;

import lombok.Getter;

/**
 * 전략 등록 시 사용되는 최소 운용 가능 금액 종류
 */
@Getter
public enum MinimumInvestmentAmount {
    UNDER_10K("1만원 ~ 500만원"),
    UP_TO_500K("500만원"),
    UP_TO_1M("1000만원"),
    UP_TO_2M("2000만원"),
    UP_TO_5M("5000만원"),
    FROM_5M_TO_10M("5000만원 ~ 1억"),
    FROM_10M_TO_20M("1억 ~ 2억"),
    FROM_20M_TO_30M("2억 ~ 3억"),
    FROM_30M_TO_40M("3억 ~ 4억"),
    FROM_40M_TO_50M("4억 ~ 5억"),
    FROM_50M_TO_100M("5억 ~ 10억"),
    ABOVE_100M("10억 이상");

    private final String description;

    MinimumInvestmentAmount(String description) {
        this.description = description;
    }
}