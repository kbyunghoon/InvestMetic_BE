package com.investmetic.domain.subscription.repository;

import com.investmetic.domain.subscription.model.entity.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionRepositoryCustom {
    @Query("SELECT d FROM Subscription d WHERE d.strategy.strategyId = :strategyId and d.user.userId = :userId")
    Optional<Subscription> findByStrategyIdAndUserId(Long strategyId, Long userId);

    List<Subscription> findAllByUserUserId(Long userId);


}
