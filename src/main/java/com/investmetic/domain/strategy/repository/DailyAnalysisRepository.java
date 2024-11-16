package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyAnalysisRepository extends JpaRepository<DailyAnalysis, Long>, DailyAnalysisRepositoryCustom {
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId")
    Page<DailyAnalysis> findByStrategyId(@Param("strategyId") Long strategyId, Pageable pageable);
}
