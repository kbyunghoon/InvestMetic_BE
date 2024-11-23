package com.investmetic.domain.subscription.repository;

public interface SubscriptionRepositoryCustom {
    boolean existsByStrategyIdAndUserId(Long strategyId, Long userId);
}