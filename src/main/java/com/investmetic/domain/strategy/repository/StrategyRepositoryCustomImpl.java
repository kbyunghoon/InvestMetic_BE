
package com.investmetic.domain.strategy.repository;

import static com.investmetic.domain.strategy.model.entity.QStockType.stockType;
import static com.investmetic.domain.strategy.model.entity.QStockTypeGroup.stockTypeGroup;
import static com.investmetic.domain.strategy.model.entity.QStrategy.strategy;
import static com.investmetic.domain.strategy.model.entity.QStrategyStatistics.strategyStatistics;
import static com.investmetic.domain.strategy.model.entity.QTradeType.tradeType;
import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.strategy.dto.response.QStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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

        StrategyDetailResponse strategyDetailResponse = queryFactory
                .select(new QStrategyDetailResponse(
                        strategy.strategyName,
                        // 종목 아이콘 URL 리스트 서브쿼리
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

        return strategyDetailResponse;
    }

    private @NotNull List<String> getStockTypeIconURLs(List<Tuple> stockTypes, StringPath stockType) {
        return stockTypes.stream()
                .map(tuple -> tuple.get(stockType))
                .collect(Collectors.toList());
    }

}

