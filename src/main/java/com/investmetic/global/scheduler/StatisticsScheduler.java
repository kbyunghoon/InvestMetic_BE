package com.investmetic.global.scheduler;

import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticsScheduler {

    private final DailyAnalysisRepository dailyAnalysisRepository;
}
