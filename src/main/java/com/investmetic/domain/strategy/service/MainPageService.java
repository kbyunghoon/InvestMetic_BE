package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.TotalRateDto;
import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.dto.response.TotalStrategyMetricsResponseDto;
import com.investmetic.domain.strategy.model.entity.QStrategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.repository.UserRepository;
import com.querydsl.core.types.OrderSpecifier;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final StrategyService strategyService;
    private final StrategyRepository strategyRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;

    private static final int TOP_SUBSCRIBER_OFFSET = 3;
    private static final int TOP_SMSCORE_OFFSET = 5;

    public List<TopRankingStrategyResponseDto> getTopSubscriberStrategy() {
        OrderSpecifier<?> orderBy = QStrategy.strategy.subscriptionCount.desc();
        List<TopRankingStrategyResponseDto> contents = strategyRepository.findTopRankingStrategy(orderBy,
                TOP_SUBSCRIBER_OFFSET);
        return fillProfitRateChartData(contents);
    }

    public List<TopRankingStrategyResponseDto> getTopSmscoreStrategy() {
        OrderSpecifier<?> orderBy = QStrategy.strategy.smScore.desc();
        List<TopRankingStrategyResponseDto> contents = strategyRepository.findTopRankingStrategy(orderBy,
                TOP_SMSCORE_OFFSET);
        return fillProfitRateChartData(contents);
    }

    public TotalStrategyMetricsResponseDto getMetricsByDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // fixme 현제 시간으로 바뀌어야 됨
        LocalDate endDate = LocalDate.of(2023,12,13);
        LocalDate startDate = endDate.minusYears(1);
        // 네이티브 쿼리 실행
        List<Object[]> results = dailyAnalysisRepository.findMetricsByDateRange(startDate.format(formatter), endDate.format(formatter));

        // x축 데이터 (dates)
        List<String> dates = results.stream()
                .map(result -> result[0].toString()) // 날짜 데이터를 String으로 변환
                .toList();

        // y축 데이터 (data)
        Map<String, List<Double>> data = Map.of(
                "avgReferencePrice", results.stream()
                        .map(result -> (Double) result[1]) // 평균 Reference Price
                        .toList(),
                "highestSmScoreReferencePrice", results.stream()
                        .map(result -> (Double) result[2]) // SM Score가 가장 높은 Reference Price
                        .toList(),
                "highestSubscribeScoreReferencePrice", results.stream()
                        .map(result -> (Double) result[3]) // 구독 수가 가장 높은 Reference Price
                        .toList()
        );

        // DTO 반환
        return TotalStrategyMetricsResponseDto.builder()
                .dates(dates)
                .data(data)
                .build();
    }

    public TotalRateDto getTotalRate() {
        return TotalRateDto.builder()
                .totalStrategies(strategyRepository.count())
                .totalSubscribe(subscriptionRepository.count())
                .totalTrader(userRepository.countByRole(Role.TRADER))
                .totalInvester(userRepository.countByRole(Role.INVESTOR))
                .build();
    }

    private List<TopRankingStrategyResponseDto> fillProfitRateChartData(List<TopRankingStrategyResponseDto> contents) {
        contents.forEach(response -> {
            Long strategyId = response.getStrategyId();
            List<Double> profitRateData = strategyRepository.findProfitRateData(strategyId);
            response.updateProfitRateChartData(profitRateData);
        });
        return contents;
    }

}
