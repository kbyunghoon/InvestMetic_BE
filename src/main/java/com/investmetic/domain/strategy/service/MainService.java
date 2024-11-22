package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.TopSubscriberStrategyResponseDto;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    private final StrategyService strategyService;
    private final StrategyRepository strategyRepository;

    public List<TopSubscriberStrategyResponseDto> getTopSubscriberStrategy() {
        List<TopSubscriberStrategyResponseDto> contents = strategyRepository.findTopSubscribeStrategy();
        contents.forEach(response-> {
                    Long strategyId = response.getStrategyId();
                    List<Double> profitRateData =strategyRepository.findProfitRateData(strategyId);
                    response.updateProfitRateChartData(profitRateData);
                }
        );
        return contents;
    }
}
