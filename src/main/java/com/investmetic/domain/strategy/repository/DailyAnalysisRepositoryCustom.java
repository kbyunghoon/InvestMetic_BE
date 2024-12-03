package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.TotalStrategyMetricsResponseDto;
import com.investmetic.domain.strategy.model.AnalysisOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DailyAnalysisRepositoryCustom {
    StrategyAnalysisResponse findStrategyAnalysis(Long strategyId, AnalysisOption option1, AnalysisOption option2);

    List<String> findXAxis(Long strategyId);

    List<Double> findYAxis(Long strategyId, AnalysisOption option);

    Page<DailyAnalysisResponse> findByStrategyId(Long strategyId, Pageable pageable);

    List<DailyAnalysisResponse> findDailyAnalysisForExcel(Long strategyId);

    Page<DailyAnalysisResponse> findMyDailyAnalysis(Long strategyId, Pageable pageable);




}
