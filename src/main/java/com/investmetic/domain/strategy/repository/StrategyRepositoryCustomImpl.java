
package com.investmetic.domain.strategy.repository;

import static com.investmetic.domain.strategy.model.entity.QDailyAnalysis.dailyAnalysis;
import static com.investmetic.domain.strategy.model.entity.QStockType.stockType;
import static com.investmetic.domain.strategy.model.entity.QStockTypeGroup.stockTypeGroup;
import static com.investmetic.domain.strategy.model.entity.QStrategy.strategy;
import static com.investmetic.domain.strategy.model.entity.QStrategyStatistics.strategyStatistics;
import static com.investmetic.domain.strategy.model.entity.QTradeType.tradeType;
import static com.investmetic.domain.subscription.model.entity.QSubscription.subscription;
import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.strategy.dto.RangeDto;
import com.investmetic.domain.strategy.dto.request.AlgorithmSearchRequest;
import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.QStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.common.QStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.model.AlgorithmType;
import com.investmetic.domain.strategy.model.DurationRange;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.ProfitRange;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class StrategyRepositoryCustomImpl implements StrategyRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public StrategyDetailResponse findStrategyDetail(Long strategyId) {

        List<Tuple> stockTypes = queryFactory
                .select(stockType.stockTypeIconURL, stockType.stockTypeName)
                .from(stockTypeGroup)
                .join(stockTypeGroup.stockType, stockType)
                .where(stockTypeGroup.strategy.strategyId.eq(strategyId))
                .fetch();

        // 종목 아이콘 목록
        List<String> stockTypeIconURLs = getStockTypeIconURLs(stockTypes, stockType.stockTypeIconURL);

        // 종목 이름목록
        List<String> stockTypeNames = getStockTypeIconURLs(stockTypes, stockType.stockTypeName);

        return queryFactory
                .select(new QStrategyDetailResponse(
                        strategy.strategyName,
                        Expressions.constant(stockTypeIconURLs), // List를 Expression으로 변환
                        tradeType.tradeTypeIconURL,
                        Expressions.constant(stockTypeNames), // List를 Expression으로 변환
                        tradeType.tradeTypeName,
                        strategy.operationCycle,
                        strategy.strategyDescription,
                        strategyStatistics.cumulativeProfitRate,
                        strategyStatistics.maxDrawdownRate,
                        strategyStatistics.averageProfitLossRate,
                        strategyStatistics.profitFactor,
                        strategyStatistics.winRate,
                        strategy.subscriptionCount,
                        user.imageUrl,
                        user.nickname,
                        strategy.minimumInvestmentAmount,
                        strategyStatistics.initialInvestment,
                        strategyStatistics.kpRatio,
                        strategyStatistics.smScore,
                        strategyStatistics.finalProfitLossDate,
                        strategy.createdAt))
                .from(strategy)
                .join(strategy.strategyStatistics, strategyStatistics)
                .join(strategy.tradeType, tradeType)
                .join(strategy.user, user)
                .where(strategy.strategyId.eq(strategyId))
                .fetchOne();
    }

    private List<Tuple> getStockTypeInfos(Long strategyId) {
        return queryFactory
                .select(stockType.stockTypeIconURL, stockType.stockTypeName)
                .from(stockTypeGroup)
                .join(stockTypeGroup.stockType, stockType)
                .where(stockTypeGroup.strategy.strategyId.eq(strategyId))
                .fetch();
    }

    private @NotNull List<String> getStockTypeIconURLs(List<Tuple> stockTypes, StringPath stockType) {
        return stockTypes.stream()
                .map(tuple -> tuple.get(stockType))
                .toList();
    }

    /***
     * 항목별 검색 조회 쿼리(운용방식, 운용주기, 운용종목, 기간, 수익률, 원금, MDD, SM Score로 검색)
     * 각 항목들은 중복 체크가 가능
     * 공개중인 전략과 승인완료된 전략만 조회가능
     * 수익률로 정렬
     * 페이징
     */
    @Override
    public Page<StrategySimpleResponse> searchByFilters(FilterSearchRequest request, Long userId,
                                                        Pageable pageable) {

        List<StrategySimpleResponse> content = queryFactory
                .select(new QStrategySimpleResponse(
                        strategy.strategyId,
                        strategy.strategyName,
                        user.imageUrl,
                        user.nickname,
                        tradeType.tradeTypeIconURL,
                        strategyStatistics.maxDrawdown,
                        strategyStatistics.smScore,
                        strategyStatistics.cumulativeProfitRate,
                        strategyStatistics.recentYearProfitRate,
                        strategy.subscriptionCount,
                        strategy.averageRating,
                        strategy.reviewCount
                ))
                .from(strategy)
                .join(strategy.strategyStatistics, strategyStatistics)
                .join(strategy.tradeType, tradeType)
                .join(strategy.user, user)
                .join(stockTypeGroup).on(stockTypeGroup.strategy.eq(strategy))
                .join(stockTypeGroup.stockType, stockType)
                .where(isApprovedAndPublic(), applyAllFilters(request))
                .orderBy(strategyStatistics.cumulativeProfitRate.desc()) // 누적수익률으로 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 count 쿼리 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(Wildcard.count)
                .from(strategy)
                .where(isApprovedAndPublic(), applyAllFilters(request));

        // 만약 페이지의 처음이나, 끝일때, 전체 데이터 크기가 pageSize보다 작은 경우 COUNT 쿼리가 실행되지 않음
        // 그 외 경우에만 fetchOne() 을 실행하여 전체 데이터 개수를 계산
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /***
     * 검색어(전략명) + 알고리즘 검색
     * 알고리즘은 중복체크 X, 알고리즘별로 정렬
     * 공개중인 전략과 승인완료된 전략만 조회가능
     * 페이징
     */
    @Override
    public Page<StrategySimpleResponse> searchByAlgorithm(AlgorithmSearchRequest request, Long userId,
                                                          Pageable pageable) {

        List<StrategySimpleResponse> content = queryFactory
                .select(new QStrategySimpleResponse(
                        strategy.strategyId,
                        strategy.strategyName,
                        user.imageUrl,
                        user.nickname,
                        tradeType.tradeTypeIconURL,
                        strategyStatistics.maxDrawdown,
                        strategyStatistics.smScore,
                        strategyStatistics.cumulativeProfitRate,
                        strategyStatistics.recentYearProfitRate,
                        strategy.subscriptionCount,
                        strategy.averageRating,
                        strategy.reviewCount
                ))
                .from(strategy)
                .join(strategy.strategyStatistics, strategyStatistics)
                .join(strategy.tradeType, tradeType)
                .join(strategy.user, user)
                .where(isApprovedAndPublic(), applySearchWordFilter(request.getSearchWord()))
                .orderBy(getOrderByAlgorithm(request.getAlgorithmType())) // 알고리즘별로 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 count 쿼리 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(Wildcard.count)
                .from(strategy)
                .where(isApprovedAndPublic());

        // 만약 페이지의 처음이나, 끝일때, 전체 데이터 크기가 pageSize보다 작은 경우 COUNT 쿼리가 실행되지 않음
        // 그 외 경우에만 fetchOne() 을 실행하여 전체 데이터 개수를 계산
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // ENUM에서 알고리즘 공식 처리 (전략 패턴)
    private OrderSpecifier<?> getOrderByAlgorithm(AlgorithmType algorithmType) {
        return algorithmType.getOrderSpecifier(strategyStatistics);
    }

    // 수익률 그래프 데이터 조회 배치 쿼리
    @Override
    public Map<Long, List<Tuple>> findProfitRateDataMap(List<Long> strategyIdS) {
        return queryFactory
                .select(dailyAnalysis.strategy.strategyId, dailyAnalysis.dailyDate,
                        dailyAnalysis.cumulativeProfitLossRate)
                .from(dailyAnalysis)
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(dailyAnalysis.strategy.strategyId)
                ));
    }

    // 종목 아이콘목록 조회 배치 쿼리
    @Override
    public Map<Long, List<String>> findStockTypeIconsMap(List<Long> strategyIdS) {
        // 종목 아이콘 조회
        List<Tuple> stockTypeIcons = queryFactory
                .select(stockTypeGroup.strategy.strategyId, stockType.stockTypeIconURL)
                .from(stockTypeGroup)
                .join(stockTypeGroup.stockType, stockType)
                .fetch();

        Map<Long, List<String>> stockTypeIconUrlsMap = new HashMap<>();

        for (Tuple stockTypeIcon : stockTypeIcons) {
            Long strategyId = stockTypeIcon.get(stockTypeGroup.strategy.strategyId);
            String iconUrl = stockTypeIcon.get(stockType.stockTypeIconURL);
            stockTypeIconUrlsMap
                    .computeIfAbsent(strategyId, key -> new ArrayList<>()) // 전략 id가 없으면 빈 리스트 생성
                    .add(iconUrl);
        }
        return stockTypeIconUrlsMap;
    }

    // 구독여부 조회 배치 쿼리
    @Override
    public Map<Long, Boolean> findBySubscriptionMap(Long userId, List<Long> strategyIdS) {
        // 구독 여부 조회
        return queryFactory
                .select(subscription.strategy.strategyId, subscription.user.userId)
                .from(subscription)
                .where(subscription.user.userId.eq(userId))
                .fetch()
                .stream().collect(Collectors.toMap(
                        tuple -> tuple.get(subscription.strategy.strategyId),
                        tuple -> true
                ));
    }

    // 모든 필터 적용
    private BooleanBuilder applyAllFilters(FilterSearchRequest filterSearchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        // 조건 추가
        builder.and(applySearchWordFilter(filterSearchRequest.getSearchWord()));
        builder.and(applyTradeTypeFilter(filterSearchRequest.getTradeTypeNames()));
        builder.and(applyOperationCycleFilter(filterSearchRequest.getOperationCycles()));
        builder.and(applyStockTypeFilter(filterSearchRequest.getStockTypeNames()));
        builder.and(applyOperationPeriodFilter(filterSearchRequest.getDurations()));
        builder.and(applyProfitRangeFilter(filterSearchRequest.getProfitRanges()));
        builder.and(applyPrincipalRangeFilter(filterSearchRequest.getPrincipalRange()));
        builder.and(applyMddRangeFilter(filterSearchRequest.getMddRange()));
        builder.and(applySmScoreRangeFilter(filterSearchRequest.getSmScoreRange()));

        return builder;
    }


    private BooleanExpression isApprovedAndPublic() {
        return strategy.isApproved.eq(IsApproved.APPROVED)
                .and(strategy.isPublic.eq(IsPublic.PUBLIC));
    }

    // 전략명 검색어 필터
    private BooleanExpression applySearchWordFilter(String searchWord) {
        return searchWord == null || searchWord.isEmpty() ? null : strategy.strategyName.like("%" + searchWord + "%");
    }

    // 운용방식(매매유형) 필터
    private BooleanExpression applyTradeTypeFilter(List<String> tradeTypeNames) {
        return tradeTypeNames == null ? null : tradeType.tradeTypeName.in(tradeTypeNames);
    }

    // 운용주기 필터 (데이/포지션)
    private BooleanExpression applyOperationCycleFilter(List<OperationCycle> operationCycles) {
        return operationCycles == null ? null : strategy.operationCycle.in(operationCycles);
    }

    // 운용종목 필터
    private BooleanExpression applyStockTypeFilter(List<String> stockTypeNames) {
        return stockTypeNames == null ? null : stockType.stockTypeName.in(stockTypeNames);
    }

    // 운용기간 필터
    private BooleanExpression applyOperationPeriodFilter(List<DurationRange> durationRanges) {
        if (durationRanges == null || durationRanges.isEmpty()) {
            return null;
        }

        BooleanExpression condition = null;
        for (DurationRange range : durationRanges) {
            BooleanExpression rangeCondition = strategyStatistics.operationPeriod.between(range.getMinDays(),
                    range.getMaxDays());
            condition = (condition == null) ? rangeCondition : condition.or(rangeCondition);
        }

        return condition;
    }

    // 수익률 필터
    private BooleanExpression applyProfitRangeFilter(List<ProfitRange> profitRanges) {
        if (profitRanges == null || profitRanges.isEmpty()) {
            return null;
        }

        BooleanExpression condition = null;
        for (ProfitRange range : profitRanges) {
            BooleanExpression rangeCondition = strategyStatistics.operationPeriod.between(range.getMinRate(),
                    range.getMaxRate());
            condition = (condition == null) ? rangeCondition : condition.or(rangeCondition);
        }

        return condition;

    }

    // 원금 필터
    private BooleanExpression applyPrincipalRangeFilter(RangeDto principalRange) {
        return principalRange == null ? null
                : strategyStatistics.principal.between(principalRange.getMin(), principalRange.getMax());
    }

    // MDD 필터
    private BooleanExpression applyMddRangeFilter(RangeDto mddRange) {
        return mddRange == null ? null
                : strategyStatistics.maxDrawdown.between(mddRange.getMin(), mddRange.getMax());
    }

    // SM Score 필터
    private BooleanExpression applySmScoreRangeFilter(RangeDto smScoreRange) {
        return smScoreRange == null ? null
                : strategyStatistics.smScore.between(smScoreRange.getMin(), smScoreRange.getMax());
    }

}

