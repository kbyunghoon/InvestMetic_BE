package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {

    /***
     * mdd 순위는 작을수록 높음 <br>
     * 수익률 표준편차 순위는 작을수록 높음 <br>
     * 승률 순위는 클수록 높음 <br>
     */
    @Modifying
    @Query(value = """
                UPDATE strategy_statistics s
                JOIN (
                    SELECT 
                        strategy_statistics_id, 
                        RANK() OVER (ORDER BY max_drawdown_rate ASC) AS mddRank,
                        RANK() OVER (ORDER BY daily_profit_loss_std_dev ASC) AS stdDevRank,
                        RANK() OVER (ORDER BY win_rate DESC) AS winRateRank
                    FROM strategy_statistics
                ) ranked
                ON s.strategy_statistics_id = ranked.strategy_statistics_id
                SET s.mdd_rank = ranked.mddRank,
                    s.std_dev_rank = ranked.stdDevRank,
                    s.win_rate_rank = ranked.winRateRank
            """, nativeQuery = true)
    void updateRanks();
}
