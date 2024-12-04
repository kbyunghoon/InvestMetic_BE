package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StrategyRepository extends JpaRepository<Strategy, Long>, StrategyRepositoryCustom {

    List<Strategy> findAllByUserUserId(Long userId);

    Boolean existsByStrategyId(Long StrategyId);

    @Query(value = """
            SELECT strategy_id, daily_date, cumulative_profit_loss_rate
            FROM (
                SELECT
                    strategy_id,
                    daily_date,
                    cumulative_profit_loss_rate,
                    ROW_NUMBER() OVER (PARTITION BY strategy_id ORDER BY daily_date DESC) AS row_num
                FROM daily_analysis
                WHERE strategy_id IN (:strategyIds)
            ) AS ranked
            WHERE row_num <= 20
            ORDER BY strategy_id ASC, daily_date ASC;
            """,
            nativeQuery = true)
    List<Object[]> findTop20ProfitRatesByStrategyIds(@Param("strategyIds") List<Long> strategyIds);
}
