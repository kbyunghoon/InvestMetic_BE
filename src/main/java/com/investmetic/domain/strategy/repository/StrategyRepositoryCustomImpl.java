package com.investmetic.domain.strategy.repository;

import static com.investmetic.domain.strategy.model.entity.QStockType.stockType;
import static com.investmetic.domain.strategy.model.entity.QStockTypeGroup.stockTypeGroup;
import static com.investmetic.domain.strategy.model.entity.QStrategy.strategy;
import static com.investmetic.domain.strategy.model.entity.QTradeType.tradeType;

import com.investmetic.domain.strategy.dto.response.QStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StrategyRepositoryCustomImpl implements StrategyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public StrategyDetailResponse findStrategyDetail(Long strategyId, Long userId) {
        return jpaQueryFactory
                .select(new QStrategyDetailResponse(
                        strategy.strategyName,
                        JPAExpressions.select(stockType.stockTypeIconURL)
                                .from(stockTypeGroup)
                                .join(stockTypeGroup.stockType, stockType)
                                .where(stockTypeGroup.strategy.strategyId.eq(strategyId))
                                .fetch(),
                        tradeType.tradeIconPath,
                        JPAExpressions.select(stockType.stockTypeName)
                                .from(stockTypeGroup)
                                .join(stockTypeGroup.stockType, stockType)
                                .where(stockTypeGroup.strategy.strategyId.eq(strategyId))
                                .fetch(),
                        tradeType.tradeName,
                        strategy.operationCycle,
                        strategy.strategyDescription,


                        )
                );
    }

}
