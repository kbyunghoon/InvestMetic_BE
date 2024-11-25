package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.model.entity.QStrategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.querydsl.core.types.OrderSpecifier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    private final StrategyService strategyService;
    private final StrategyRepository strategyRepository;

    public List<TopRankingStrategyResponseDto> getTopSubscriberStrategy() {
        int offset = 3;
        OrderSpecifier<?> orderBy = QStrategy.strategy.subscriptionCount.desc();
        List<TopRankingStrategyResponseDto> contents = strategyRepository.findTopRankingStrategy(orderBy,offset);
        contents.forEach(response-> {
                    Long strategyId = response.getStrategyId();
                    List<Double> profitRateData =strategyRepository.findProfitRateData(strategyId);
                    response.updateProfitRateChartData(profitRateData);
                }
        );
        return contents;
    }
    public List<TopRankingStrategyResponseDto> getTopSmscoreStrategy() {
        int offset = 5;
        OrderSpecifier<?> orderBy = QStrategy.strategy.smScore.desc();
        List<TopRankingStrategyResponseDto> contents = strategyRepository.findTopRankingStrategy(orderBy,offset);
        contents.forEach(response-> {
                    Long strategyId = response.getStrategyId();
                    List<Double> profitRateData =strategyRepository.findProfitRateData(strategyId);
                    response.updateProfitRateChartData(profitRateData);
                }
        );
        return contents;
    }
}
