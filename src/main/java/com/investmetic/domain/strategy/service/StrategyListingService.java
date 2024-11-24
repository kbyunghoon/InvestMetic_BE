package com.investmetic.domain.strategy.service;

import static com.investmetic.domain.strategy.model.entity.QDailyAnalysis.dailyAnalysis;

import com.investmetic.domain.strategy.dto.ProfitRateChartDto;
import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.common.BaseStrategyResponse;
import com.investmetic.domain.strategy.dto.response.common.MyStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.model.AlgorithmType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.common.PageResponseDto;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//TODO : 조회 성능개선 필요
@RequiredArgsConstructor
@Service
public class StrategyListingService {

    private final StrategyRepository strategyRepository;

    /**
     * 필터 기반 전략목록 조회 (구독 여부 포함)
     *
     * @param request  전략 검색 필터 요청
     * @param userId   로그인한 유저id
     * @param pageable 페이징 정보
     * @return
     */
    public PageResponseDto<StrategySimpleResponse> searchByFilters(FilterSearchRequest request, Long userId,
                                                                   Pageable pageable) {
        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchByFilters(request, pageable);
        return updateResponsesWithSubscription(userId, content);
    }

    /**
     * 알고리즘 기반 전략목록 조회(구독여부 포함)
     *
     * @param searchWord    검색어
     * @param algorithmType 알고리즘별 타입
     * @param userId        로그인한 유저id
     * @param pageable      페이징 정보
     * @return
     */
    public PageResponseDto<StrategySimpleResponse> searchByAlgorithm(String searchWord, AlgorithmType algorithmType,
                                                                     Long userId, Pageable pageable) {
        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchByAlgorithm(searchWord, algorithmType,
                pageable);
        return updateResponsesWithSubscription(userId, content);
    }

    /**
     * 나의 전략목록 조회 (트레이더), 구독여부 미포함
     *
     * @param userId   로그인한 트레이더 id
     * @param pageable 페이징 정보
     * @return
     */
    public PageResponseDto<MyStrategySimpleResponse> getMyStrategies(Long userId, Pageable pageable) {
        Page<MyStrategySimpleResponse> content = strategyRepository.findMyStrategies(userId, pageable);
        return updateResponsesWithoutSubscription(userId, content);
    }

    private PageResponseDto<StrategySimpleResponse> updateResponsesWithSubscription(Long userId,
                                                                                    Page<StrategySimpleResponse> content) {
        // 전략 ID 추출
        List<Long> strategyIds = getStrategyIds(content);

        // 배치 쿼리를 통해 필요한 데이터 조회
        Map<Long, List<String>> stockTypeIconsMap = strategyRepository.findStockTypeIconsMap(strategyIds);
        Map<Long, List<Tuple>> profitRateDataMap = strategyRepository.findProfitRateDataMap(strategyIds);
        Map<Long, Boolean> subscriptionMap = (userId != null)
                ? strategyRepository.findBySubscriptionMap(userId, strategyIds)
                : strategyIds.stream().collect(Collectors.toMap(id -> id, id -> false));

        // 응답 데이터 업데이트
        updateContent(content, stockTypeIconsMap, subscriptionMap, profitRateDataMap);

        return new PageResponseDto<>(content);
    }

    private PageResponseDto<MyStrategySimpleResponse> updateResponsesWithoutSubscription(Long userId,
                                                                                         Page<MyStrategySimpleResponse> content) {
        // 전략 ID 추출
        List<Long> strategyIds = getMyStrategyIds(content);

        // 배치 쿼리를 통해 필요한 데이터 조회
        Map<Long, List<String>> stockTypeIconsMap = strategyRepository.findStockTypeIconsMap(strategyIds);
        Map<Long, List<Tuple>> profitRateDataMap = strategyRepository.findProfitRateDataMap(strategyIds);

        // 응답 데이터 업데이트
        updateContent(content, stockTypeIconsMap, null, profitRateDataMap);

        return new PageResponseDto<>(content);
    }


    private List<Long> getStrategyIds(Page<StrategySimpleResponse> content) {
        return content.getContent()
                .stream()
                .map(StrategySimpleResponse::getStrategyId)
                .toList();
    }

    private List<Long> getMyStrategyIds(Page<MyStrategySimpleResponse> content) {
        return content.getContent()
                .stream()
                .map(MyStrategySimpleResponse::getStrategyId)
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
    private <T extends BaseStrategyResponse> void updateContent(Page<T> content,
                                                                Map<Long, List<String>> stockTypeIconsMap,
                                                                Map<Long, Boolean> subscriptionMap,
                                                                Map<Long, List<Tuple>> profitRateDataMap) {

        content.forEach(response -> {
            Long strategyId = response.getStrategyId();

            // 종목 아이콘 업데이트
            response.updateStockTypeIconUrls(stockTypeIconsMap.getOrDefault(strategyId, List.of()));

            // 구독 여부 업데이트
            if (subscriptionMap != null) {
                response.updateIsSubscribed(subscriptionMap.getOrDefault(strategyId, false));
            }

            // 수익률 그래프 업데이트
            List<Tuple> profitRateData = profitRateDataMap.getOrDefault(strategyId, List.of());

            ProfitRateChartDto profitRateChartData = ProfitRateChartDto.builder()
                    .dates(profitRateData.stream()
                            .map(tuple -> tuple.get(dailyAnalysis.dailyDate.stringValue())).toList())
                    .profitRates(profitRateData.stream()
                            .map(tuple -> tuple.get(dailyAnalysis.cumulativeProfitLossRate)).toList())
                    .build();

            response.updateProfitRateChartData(profitRateChartData);
        });
    }

}
