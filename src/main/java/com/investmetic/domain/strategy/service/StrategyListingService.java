package com.investmetic.domain.strategy.service;

import static java.util.stream.Collectors.toList;

import com.investmetic.domain.strategy.dto.ProfitRateChartDto;
import com.investmetic.domain.strategy.dto.StockTypeInfo;
import com.investmetic.domain.strategy.dto.request.SearchRequest;
import com.investmetic.domain.strategy.dto.response.ProfitRateData;
import com.investmetic.domain.strategy.dto.response.SearchInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.common.BaseStrategyResponse;
import com.investmetic.domain.strategy.dto.response.common.MyStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.common.PageResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//TODO : 조회 성능개선 예정
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StrategyListingService {

    private final StrategyRepository strategyRepository;
    private final StockTypeRepository stockTypeRepository;
    private final TradeTypeRepository tradeTypeRepository;


    /**
     * 항목 + 알고리즘 기반 전략목록 조회 (구독여부 포함)
     *
     * @param request  전략 검색필터 요청
     * @param userId   로그인한 유저id
     * @param pageable 페이징 정보
     * @return 전략 목록에 대한 페이지 응답
     */
    public PageResponseDto<StrategySimpleResponse> search(SearchRequest request, Long userId, Pageable pageable) {
        // 데이터 조회
        Page<StrategySimpleResponse> content = strategyRepository.searchBy(request, pageable);
        Map<Long, Boolean> subscriptionMap = generateSubscriptionMap(userId, getStrategyIds(content));
        return processStrategyResponses(content, subscriptionMap);
    }

    /**
     * 나의 전략목록 조회 (트레이더) - 구독여부 미포함
     *
     * @param userId   로그인한 트레이더 정보
     * @param pageable 페이징 정보
     * @return 나의 전략 목록에 대한 페이지 응답
     */
    public PageResponseDto<MyStrategySimpleResponse> getMyStrategies(Long userId, Pageable pageable) {
        Page<MyStrategySimpleResponse> content = strategyRepository.findMyStrategies(userId, pageable);
        return processStrategyResponses(content, null);
    }

    /**
     * 구독한 전략 목록 조회 - 구독여부 항상 true
     *
     * @param userId   : 로그인한 회원정보(트레이더, 투자자)
     * @param pageable : 페이징 정보
     * @return 구독한 전략 목록에 대한 페이지 응답
     */
    public PageResponseDto<StrategySimpleResponse> getSubscribedStrategies(Long userId, Pageable pageable) {
        Page<StrategySimpleResponse> content = strategyRepository.findSubscribedStrategies(userId,
                pageable);
        Map<Long, Boolean> subscriptionMap = getStrategyIds(content).stream()
                .collect(Collectors.toMap(id -> id, id -> true));
        return processStrategyResponses(content, subscriptionMap);
    }

    /**
     * 응답 데이터 공통 처리 로직
     *
     * @param content         : 조회된 전략 데이터
     * @param subscriptionMap 구독 여부 데이터 (null일 경우 구독 여부 처리 안 함)
     * @param <T>             전략 응답 타입 (StrategySimpleResponse 또는 MyStrategySimpleResponse)
     */
    private <T extends BaseStrategyResponse> PageResponseDto<T> processStrategyResponses(
            Page<T> content, Map<Long, Boolean> subscriptionMap) {
        // 전략 ID 추출
        List<Long> strategyIds = getStrategyIds(content);

        // 각 전략 ID에 대한 배치 쿼리 조회 (성능개선)
        Map<Long, StockTypeInfo> stockTypeInfoMap = strategyRepository.findStockTypeInfoMap(strategyIds);
        Map<Long, ProfitRateChartDto> profitRateDataMap = getProfitRateDataForStrategies(strategyIds);

        // 응답 데이터 업데이트
        updateContent(content, stockTypeInfoMap, subscriptionMap, profitRateDataMap);

        return new PageResponseDto<>(content);
    }


    private Map<Long, ProfitRateChartDto> getProfitRateDataForStrategies(List<Long> strategyIds) {
        // 리포지토리에서 원본 데이터 가져오기
        List<ProfitRateData> profitRateDatas = findTop20ProfitRatesByStrategyIds(strategyIds);

        // strategyId별로 데이터를 그룹화
        return profitRateDatas.stream()
                .collect(Collectors.groupingBy(
                        ProfitRateData::getStrategyId,
                        Collectors.collectingAndThen(
                                toList(),
                                this::convertToProfitRateChartDto
                        )
                ));
    }

    private List<ProfitRateData> findTop20ProfitRatesByStrategyIds(List<Long> strategyIds) {
        List<Object[]> rawResults = strategyRepository.findTop20ProfitRatesByStrategyIds(strategyIds);

        return rawResults.stream()
                .map(row -> ProfitRateData.builder()
                        .strategyId((Long) row[0])
                        .dailyDate(((java.sql.Date) row[1]).toLocalDate())
                        .cumulativeProfitLossRate((Double) row[2])
                        .build()
                ).toList();
    }

    /**
     * 구독 여부 맵 생성
     *
     * @param userId      로그인한 유저 ID
     * @param strategyIds 페이징으로 조회된 전략의 ID 목록
     * @return 구독 여부 데이터 맵 (key : 전략id ,value : 구독여부)
     */
    private Map<Long, Boolean> generateSubscriptionMap(Long userId, List<Long> strategyIds) {

        // 로그인 된 유저가 있으면 실제 구독여부 조회, 비로그인이면 구독여부 항상 false
        return (userId != null)
                ? strategyRepository.findBySubscriptionMap(userId, strategyIds)
                : strategyIds.stream()
                        .collect(Collectors.toMap(id -> id, id -> false));
    }

    /**
     * 전략 ID 추출 공통 로직
     *
     * @param content 조회된 전략 데이터
     * @param <T>     전략 응답 타입
     * @return 전략 ID 리스트
     */
    private <T extends BaseStrategyResponse> List<Long> getStrategyIds(Page<T> content) {
        return content.getContent()
                .stream()
                .map(BaseStrategyResponse::getStrategyId)
                .toList();
    }

    /**
     * 응답 데이터 업데이트 <br> 각 전략 응답에 대해 종목 아이콘, 구독 여부, 수익률 그래프 데이터를 설정
     *
     * @param content           전략 응답 데이터
     * @param stockTypeInfoMap  종목 아이콘,이름 데이터 맵
     * @param subscriptionMap   구독 여부 데이터 맵
     * @param profitRateDataMap 수익률 그래프 데이터 맵
     */
    private <T extends BaseStrategyResponse> void updateContent(Page<T> content,
                                                                Map<Long, StockTypeInfo> stockTypeInfoMap,
                                                                Map<Long, Boolean> subscriptionMap,
                                                                Map<Long, ProfitRateChartDto> profitRateDataMap) {

        content.forEach(response -> {
            Long strategyId = response.getStrategyId();

            // 종목 아이콘 업데이트
            StockTypeInfo stockTypeInfo = stockTypeInfoMap.get(strategyId);
            response.updateStockTypeInfo(stockTypeInfo);

            // 구독 여부 업데이트
            if (subscriptionMap != null) {
                response.updateIsSubscribed(subscriptionMap.getOrDefault(strategyId, false));
            }

            // 수익률 그래프 업데이트
            response.updateProfitRateChartData(profitRateDataMap.get(strategyId));
        });
    }

    private ProfitRateChartDto convertToProfitRateChartDto(List<ProfitRateData> profitRateDataMap) {
        List<LocalDate> dates = profitRateDataMap.stream()
                .map(ProfitRateData::getDailyDate)
                .toList();

        List<Double> profitRates = profitRateDataMap.stream()
                .map(ProfitRateData::getCumulativeProfitLossRate)
                .toList();

        return ProfitRateChartDto.builder()
                .dates(dates)
                .profitRates(profitRates)
                .build();
    }

    //TODO : 캐시 적용해야댐
    public SearchInfoResponseDto loadSearchInfo() {
        List<StockType> stockTypes = stockTypeRepository.findAll();
        List<TradeType> tradeTypes = tradeTypeRepository.findAll();
        return SearchInfoResponseDto.from(stockTypes, tradeTypes);
    }
}
