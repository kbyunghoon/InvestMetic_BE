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

        return updateStrategyResponses(userId, content);

    }

    public PageResponseDto<StrategySimpleResponse> searchByAlgorithm(AlgorithmSearchRequest request, Long userId,
                                                                     Pageable pageable) {
        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchByAlgorithm(request, userId, pageable);

        return updateStrategyResponses(userId, content);

    }

    private PageResponseDto<StrategySimpleResponse> updateStrategyResponses(Long userId,
                                                                            Page<StrategySimpleResponse> content) {
        // 전략 ID 추출
        List<Long> strategyIds = getStrategyIds(content);

        // 배치 쿼리를 통해 필요한 데이터 조회
        Map<Long, List<String>> stockTypeIconsMap = strategyRepository.findStockTypeIconsMap(strategyIds);
        Map<Long, Boolean> subscriptionMap = strategyRepository.findBySubscriptionMap(userId, strategyIds);
        Map<Long, List<Tuple>> profitRateDataMap = strategyRepository.findProfitRateDataMap(strategyIds);

        // 응답 데이터 업데이트
        updateContent(content, stockTypeIconsMap, subscriptionMap, profitRateDataMap);

        return new PageResponseDto<>(content);
    }

    private List<Long> getStrategyIds(Page<StrategySimpleResponse> content) {
        return content.getContent()
                .stream()
                .map(StrategySimpleResponse::getStrategyId)
                .toList();
    }

    /***
     * 응답 데이터 업데이트
     * 각 전략 응답에 대해 종목 아이콘, 구독 여부, 수익률 그래프 데이터를 설정
     *
     * @param content 전략 응답 데이터
     * @param stockTypeIconsMap 종목 아이콘 데이터 맵
     * @param subscriptionMap 구독 여부 데이터 맵
     * @param profitRateDataMap 수익률 그래프 데이터 맵
     */
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
