package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.MonthlyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MonthlyAnalysisRepository extends JpaRepository<MonthlyAnalysis, Long> {
    @Query("select m from MonthlyAnalysis m where m.strategy.strategyId = :strategyId")
    Page<MonthlyAnalysis> findByStrategyId(@Param("strategyId") Long strategyId, Pageable pageable);

    List<MonthlyAnalysis> findByStrategyStrategyId(Long strategyId);

    Optional<MonthlyAnalysis> findByStrategyAndMonthlyDate(Strategy strategy, LocalDate monthlyDate);

    void deleteAllByStrategy(Strategy strategy);
}
