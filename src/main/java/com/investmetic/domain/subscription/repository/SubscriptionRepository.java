package com.investmetic.domain.subscription.repository;

import com.investmetic.domain.subscription.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionRepositoryCustom {
}
