package com.investmetic.domain.subscription.repository;

import com.investmetic.domain.strategy.model.entity.Strategy;

public interface SubscriptionRepositoryCustom {
    boolean existsByStrategyIdAndUserId(Long strategyId, Long userId);

    void deleteAllByStrategy(Strategy strategy);
}