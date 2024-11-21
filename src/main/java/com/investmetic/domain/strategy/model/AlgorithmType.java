package com.investmetic.domain.strategy.model;

import com.investmetic.domain.strategy.model.entity.QStrategyStatistics;
import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public enum AlgorithmType {
    EFFICIENT_STRATEGY("효율형 전략") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QStrategyStatistics stats) {
            log.info("효율형 전략 선택");
            return stats.cumulativeProfitRate.divide(stats.maxDrawdownRate).desc();
        }
    },
    ATTACK_STRATEGY("공격형 전략") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QStrategyStatistics stats) {
            log.info("공격형 전략 선택");
            return stats.cumulativeProfitRate
                    .divide(stats.winRate.multiply(0.01).subtract(1).abs()).desc();
        }
    },
    DEFENSIVE_STRATEGY("방어형 전략") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QStrategyStatistics stats) {
            log.info("방어형 전략 선택");
            return stats.mddRank.add(stats.stdDevRank)
                    .add(stats.winRateRank).divide(3).desc();
        }
    };

    private final String description;


    public abstract OrderSpecifier<?> getOrderSpecifier(QStrategyStatistics stats);
}
