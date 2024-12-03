package com.investmetic.domain.subscription.service;

import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final StrategyRepository strategyRepository;

    @Transactional
    public void subscribe(Long strategyId, Long userId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        validateSelfSubscription(strategy, userId);

        User user = User.builder()
                .userId(userId)
                .build();

        Optional<Subscription> existingSubscription = subscriptionRepository.findByStrategyIdAndUserId(strategyId,
                userId);

        if (existingSubscription.isPresent()) {
            handleUnsubscribe(existingSubscription.get(), strategy);
        } else {
            handleSubscribe(user, strategy);
        }
    }

    /**
     * 전략 구독
     */
    private void handleUnsubscribe(Subscription subscription, Strategy strategy) {
        strategy.minusSubscriptionCount();
        subscriptionRepository.delete(subscription);
    }

    /**
     * 구독 취소
     */
    private void handleSubscribe(User user, Strategy strategy) {
        strategy.plusSubscriptionCount();
        Subscription subscription = Subscription.builder()
                .user(user)
                .strategy(strategy)
                .build();
        subscriptionRepository.save(subscription);
    }

    /**
     * 본인전략 구독 못하는 유효성 검사 추가
     */
    private void validateSelfSubscription(Strategy strategy, Long userId) {
        if (strategy.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SELF_SUBSCRIPTION_NOT_ALLOWED);
        }
    }
}
