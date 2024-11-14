package com.investmetic.domain.subscription.repository;

import static com.investmetic.domain.subscription.model.entity.QSubscription.subscription;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionRepositoryCustomImpl implements SubscriptionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByStrategyIdAndUserId(Long strategyId, Long userId) {
        Integer result = jpaQueryFactory.selectOne()
                .from(subscription)
                .where(subscription.strategy.strategyId.eq(strategyId), subscription.user.userId.eq(userId))
                .fetchFirst();
        return result != null;
    }
}
