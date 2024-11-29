package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.TotalRateDto;
import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.model.entity.QStrategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.repository.UserRepository;
import com.querydsl.core.types.OrderSpecifier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final StrategyService strategyService;
    private final StrategyRepository strategyRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

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
