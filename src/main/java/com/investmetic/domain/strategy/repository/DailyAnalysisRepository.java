package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyAnalysisRepository extends JpaRepository<DailyAnalysis, Long>, DailyAnalysisRepositoryCustom {

    boolean existsByStrategyAndDailyDate(Strategy strategy, LocalDate dailyDate);

    List<DailyAnalysis> findByStrategyStrategyId(Long strategyId);
}
