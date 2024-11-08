package com.investmetic.domain.strategy.model;

import lombok.Getter;

/**
 * 전략 등록 시 사용되는 주기 종류
 */
@Getter
public enum TradingStrategyType {
    DAY("데이"),
    POSITION("포지션");

    private final String description;

    TradingStrategyType(String description) {
        this.description = description;
    }
}