package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {

    @Query("select s from StrategyStatistics s where s.strategy.strategyId = :strategyId")
    Optional<StrategyStatistics> findByStrategy(@Param("strategyId") Long strategyId);
}
