package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.AnalysisDataDto;
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

    List<AnalysisDataDto> findSingleOptionAnalysisData(Long strategyId, AnalysisOption option);

    StrategyAnalysisResponse findStrategyAnalysisData(Long strategyId, AnalysisOption option1, AnalysisOption option2);

    Page<DailyAnalysisResponse> findByStrategyId(Long strategyId, Pageable pageable);

    List<DailyAnalysisResponse> findDailyAnalysisForExcel(Long strategyId);

    Page<DailyAnalysisResponse> findMyDailyAnalysis(Long strategyId, Pageable pageable);




}
