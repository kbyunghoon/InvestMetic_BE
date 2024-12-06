package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.StockTypeInfo;
import com.investmetic.domain.strategy.dto.request.SearchRequest;
import com.investmetic.domain.strategy.dto.response.AdminStrategyResponseDto;
import com.investmetic.domain.strategy.dto.response.MyStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.dto.response.common.MyStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.model.IsApproved;
import com.querydsl.core.types.OrderSpecifier;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StrategyRepositoryCustom {
    StrategyDetailResponse findStrategyDetail(Long strategyId);

    MyStrategyDetailResponse findMyStrategyDetail(Long strategyId);

    Map<Long, StockTypeInfo> findStockTypeInfoMap(List<Long> strategyIdS);

    Map<Long, Boolean> findBySubscriptionMap(Long userId, List<Long> strategyIdS);

    Page<StrategySimpleResponse> searchBy(SearchRequest searchRequest, Pageable pageable);

    Page<MyStrategySimpleResponse> findMyStrategies(Long userId, Pageable pageable);

    Page<StrategySimpleResponse> findSubscribedStrategies(Long userId, Pageable pageable);

    List<TopRankingStrategyResponseDto> findTopRankingStrategy(OrderSpecifier<?> orderBy, int limit);

    List<Double> findProfitRateData(Long strategyId);

    Page<AdminStrategyResponseDto> findAdminStrategies(Pageable pageable, String searchWord, IsApproved isApproved);

    Page<StrategySimpleResponse> getTraderStrategies(Long traderId, Pageable pageable);
}

