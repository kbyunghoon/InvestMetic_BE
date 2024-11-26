package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.request.SearchRequest;
import com.investmetic.domain.strategy.dto.response.MyStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.common.MyStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StrategyRepositoryCustom {
    Optional<StrategyDetailResponse> findStrategyDetail(Long strategyId);

    Optional<MyStrategyDetailResponse> findMyStrategyDetail(Long strategyId);

    Map<Long, List<String>> findStockTypeIconsMap(List<Long> strategyIdS);

    Map<Long, List<Tuple>> findProfitRateDataMap(List<Long> strategyIdS);

    Map<Long, Boolean> findBySubscriptionMap(Long userId, List<Long> strategyIdS);

    Page<StrategySimpleResponse> searchBy(SearchRequest searchRequest, Pageable pageable);

    Page<MyStrategySimpleResponse> findMyStrategies(Long userId, Pageable pageable);

    Page<StrategySimpleResponse> findSubscribedStrategies(Long userId, Pageable pageable);

}

