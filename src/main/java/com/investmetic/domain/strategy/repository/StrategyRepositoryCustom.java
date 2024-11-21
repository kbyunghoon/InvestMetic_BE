package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.request.AlgorithmSearchRequest;
import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StrategyRepositoryCustom {
    StrategyDetailResponse findStrategyDetail(Long strategyId);

    Map<Long, List<String>> findStockTypeIconsMap(List<Long> strategyIdS);

    Map<Long, List<Tuple>> findProfitRateDataMap(List<Long> strategyIdS);

    Map<Long, Boolean> findBySubscriptionMap(Long userId, List<Long> strategyIdS);

    Page<StrategySimpleResponse> searchByFilters(FilterSearchRequest filterSearchRequest, Long userId,
                                                 Pageable pageable);

    Page<StrategySimpleResponse> searchByAlgorithm(AlgorithmSearchRequest algorithmSearchRequest, Long userId,
                                                   Pageable pageable);
}
