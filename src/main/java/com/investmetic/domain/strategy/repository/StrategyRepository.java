package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrategyRepository extends JpaRepository<Strategy, Long>, StrategyRepositoryCustom {
    Boolean existsByStrategyId(Long StrategyId);
}
