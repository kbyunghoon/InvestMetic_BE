package com.investmetic.domain.strategy.repository;

import static com.investmetic.domain.strategy.model.entity.QDailyAnalysis.dailyAnalysis;

import com.investmetic.domain.strategy.dto.AnalysisDataDto;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.QDailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.model.entity.Proceed;
import com.investmetic.domain.strategy.model.entity.QDailyAnalysis;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class DailyAnalysisRepositoryCustomImpl implements DailyAnalysisRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AnalysisDataDto> findSingleOptionAnalysisData(Long strategyId, AnalysisOption option) {
        return queryFactory
                .select(Projections.constructor(AnalysisDataDto.class,
                        dailyAnalysis.dailyDate.stringValue(), // 날짜
                        findByOption(option)))                 // 옵션에 따른 Y축 데이터
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId)) // 전략 ID 조건
                .orderBy(dailyAnalysis.dailyDate.asc())                 // 날짜 오름차순 정렬
                .fetch();
    }

    @Override
    public StrategyAnalysisResponse findStrategyAnalysisData(Long strategyId, AnalysisOption option1,
                                                             AnalysisOption option2) {
        List<Tuple> results = queryFactory
                .select(
                        dailyAnalysis.dailyDate.stringValue(),
                        findByOption(option1),
                        findByOption(option2))
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId))
                .orderBy(dailyAnalysis.dailyDate.asc())
                .fetch();

        List<String> dates = new ArrayList<>();
        List<Double> firstYAxis = new ArrayList<>();
        List<Double> secondYAxis = new ArrayList<>();

        // 데이터를 날짜와 두 개의 Y축 데이터로 분리
        for (Tuple result : results) {
            dates.add(result.get(dailyAnalysis.dailyDate.stringValue()));
            firstYAxis.add(result.get(findByOption(option1)));
            secondYAxis.add(result.get(findByOption(option1)));
        }

        // 응답 데이터 매핑
        Map<String, List<Double>> yAxis = Map.of(
                option1.name(), firstYAxis,
                option2.name(), secondYAxis
        );

        return StrategyAnalysisResponse.builder()
                .dates(dates)
                .data(yAxis)
                .build();
    }


    @Override
    public Page<DailyAnalysisResponse> findByStrategyId(Long strategyId, Pageable pageable) {
        List<DailyAnalysisResponse> content = queryFactory.select(new QDailyAnalysisResponse(
                        Expressions.nullExpression(Long.class),
                        dailyAnalysis.dailyDate,
                        dailyAnalysis.principal,
                        dailyAnalysis.transaction,
                        dailyAnalysis.dailyProfitLoss,
                        dailyAnalysis.dailyProfitLossRate,
                        dailyAnalysis.cumulativeProfitLoss,
                        dailyAnalysis.cumulativeProfitLossRate))
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId), dailyAnalysis.proceed.eq(Proceed.YES))
                .orderBy(dailyAnalysis.dailyDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 count 쿼리 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(Wildcard.count)
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId), dailyAnalysis.proceed.eq(Proceed.YES));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<DailyAnalysisResponse> findDailyAnalysisForExcel(Long strategyId) {
        return queryFactory
                .select(new QDailyAnalysisResponse(
                        Expressions.nullExpression(Long.class),
                        dailyAnalysis.dailyDate,
                        dailyAnalysis.principal,
                        dailyAnalysis.transaction,
                        dailyAnalysis.dailyProfitLoss,
                        dailyAnalysis.dailyProfitLossRate,
                        dailyAnalysis.cumulativeProfitLoss,
                        dailyAnalysis.cumulativeProfitLossRate))
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId))
                .fetch();
    }

    @Override
    public Page<DailyAnalysisResponse> findMyDailyAnalysis(Long strategyId, Pageable pageable) {
        List<DailyAnalysisResponse> content = queryFactory
                .select(new QDailyAnalysisResponse(
                        dailyAnalysis.dailyAnalysisId,
                        dailyAnalysis.dailyDate,
                        dailyAnalysis.principal,
                        dailyAnalysis.transaction,
                        dailyAnalysis.dailyProfitLoss,
                        dailyAnalysis.dailyProfitLossRate,
                        dailyAnalysis.cumulativeProfitLoss,
                        dailyAnalysis.cumulativeProfitLossRate))
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId),
                        isLatestDailyAnalysis(dailyAnalysis, strategyId))
                .orderBy(dailyAnalysis.dailyDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 count 쿼리 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(Wildcard.count)
                .from(dailyAnalysis)
                .where(dailyAnalysis.strategy.strategyId.eq(strategyId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression isLatestDailyAnalysis(QDailyAnalysis d1, Long strategyId) {

        QDailyAnalysis d2 = new QDailyAnalysis("d2");

        return d1.proceed.eq(
                JPAExpressions.select(d2.proceed.min())
                        .from(d2)
                        .where(d2.strategy.strategyId.eq(strategyId)
                                .and(d2.dailyDate.eq(d1.dailyDate)))
        );
    }

    public NumberExpression<Double> findByOption(AnalysisOption option) {
        switch (option) {
            case BALANCE -> {
                return dailyAnalysis.balance.doubleValue();
            }
            case PRINCIPAL -> {
                return dailyAnalysis.principal.doubleValue();
            }
            case CUMULATIVE_TRANSACTION_AMOUNT -> {
                return dailyAnalysis.cumulativeTransactionAmount.doubleValue();
            }
            case TRANSACTION -> {
                return dailyAnalysis.transaction.doubleValue();
            }
            case DAILY_PROFIT_LOSS -> {
                return dailyAnalysis.dailyProfitLoss.doubleValue();
            }
            case DAILY_PROFIT_LOSS_RATE -> {
                return dailyAnalysis.dailyProfitLossRate;
            }
            case CUMULATIVE_PROFIT_LOSS -> {
                return dailyAnalysis.cumulativeProfitLoss.doubleValue();
            }
            case CUMULATIVE_PROFIT_LOSS_RATE -> {
                return dailyAnalysis.cumulativeProfitLossRate;
            }
            case CURRENT_DRAWDOWN -> {
                return dailyAnalysis.currentDrawdown.doubleValue();
            }
            case CURRENT_DRAWDOWN_RATE -> {
                return dailyAnalysis.currentDrawdownRate;
            }
            case AVERAGE_PROFIT_LOSS -> {
                return dailyAnalysis.averageProfitLoss.doubleValue();
            }
            case AVERAGE_PROFIT_LOSS_RATIO -> {
                return dailyAnalysis.averageProfitLossRatio;
            }
            case WIN_RATE -> {
                return dailyAnalysis.winRate;
            }
            case PROFIT_FACTOR -> {
                return dailyAnalysis.profitFactor;
            }
            case ROA -> {
                return dailyAnalysis.roa;
            }
            case TOTAL_PROFIT -> {
                return dailyAnalysis.totalProfit.doubleValue();
            }
            case TOTAL_LOSS -> {
                return dailyAnalysis.totalLoss.doubleValue();
            }
            default -> {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }

    }

}
