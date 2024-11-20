package com.investmetic.domain.strategy.service;

import static com.investmetic.domain.strategy.model.entity.QDailyAnalysis.dailyAnalysis;

import com.investmetic.domain.strategy.dto.ProfitRateChartDto;
import com.investmetic.domain.strategy.dto.request.AlgorithmSearchRequest;
import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.common.PageResponseDto;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StrategySearchService {

    private final StrategyRepository strategyRepository;

    public PageResponseDto<StrategySimpleResponse> searchByFilters(FilterSearchRequest request, Long userId,
                                                                   Pageable pageable) {

        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchByFilters(request, userId, pageable);

        // 전략 ID 추출
        List<Long> strategyIds = content.getContent()
                .stream()
                .map(StrategySimpleResponse::getStrategyId)
                .toList();

        Map<Long, List<String>> stockTypeIconsMap = strategyRepository.findStockTypeIconsMap(strategyIds); // 배치 쿼리
        Map<Long, Boolean> subscriptionMap = strategyRepository.findBySubscriptionMap(userId, strategyIds); // 배치 쿼리
        Map<Long, List<Tuple>> profitRateDataMap = strategyRepository.findProfitRateDataMap(strategyIds); // 배치 쿼리

        // 2. 데이터 업데이트
        updateContent(content, stockTypeIconsMap, subscriptionMap, profitRateDataMap);

        return new PageResponseDto<>(content);
    }

    public PageResponseDto<StrategySimpleResponse> searchByAlgorithm(AlgorithmSearchRequest request, Long userId,
                                                                     Pageable pageable) {

        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchByAlgorithm(request, userId, pageable);

        // 전략 ID 추출
        List<Long> strategyIds = content.getContent()
                .stream()
                .map(StrategySimpleResponse::getStrategyId)
                .toList();

        Map<Long, List<String>> stockTypeIconsMap = strategyRepository.findStockTypeIconsMap(strategyIds); // 배치 쿼리
        Map<Long, Boolean> subscriptionMap = strategyRepository.findBySubscriptionMap(userId, strategyIds); // 배치 쿼리
        Map<Long, List<Tuple>> profitRateDataMap = strategyRepository.findProfitRateDataMap(strategyIds); // 배치 쿼리

        // 2. 데이터 업데이트
        updateContent(content, stockTypeIconsMap, subscriptionMap, profitRateDataMap);

        return new PageResponseDto<>(content);
    }

    private void updateContent(Page<StrategySimpleResponse> content, Map<Long, List<String>> stockTypeIconsMap,
                               Map<Long, Boolean> subscriptionMap, Map<Long, List<Tuple>> profitRateDataMap) {

        content.forEach(response -> {
            Long strategyId = response.getStrategyId();

            // 종목 아이콘 업데이트
            response.updateStockTypeIconUrls(stockTypeIconsMap.getOrDefault(strategyId, List.of()));

            // 구독 여부 업데이트
            response.updateIsSubscribed(subscriptionMap.getOrDefault(strategyId, false));

            // 수익률 그래프 업데이트
            List<Tuple> profitRateData = profitRateDataMap.getOrDefault(strategyId, List.of());

            ProfitRateChartDto profitRateChartData = ProfitRateChartDto.builder()
                    .xAxis(profitRateData.stream()
                            .map(tuple -> tuple.get(dailyAnalysis.dailyDate.stringValue())).toList())
                    .yAxis(profitRateData.stream()
                            .map(tuple -> tuple.get(dailyAnalysis.cumulativeProfitLossRate)).toList())
                    .build();

            response.updateProfitRateChartData(profitRateChartData);
        });
    }
}
