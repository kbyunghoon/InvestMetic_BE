package com.investmetic.global.scheduler;


import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.global.util.DailyAnalysisCalculator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyAnalysisProcessor implements ItemProcessor<DailyAnalysis, DailyAnalysis> {
    private final DailyAnalysisRepository dailyAnalysisRepository;

    @Override
    public DailyAnalysis process(DailyAnalysis currentAnalysis) {
        return currentAnalysis;
    }
}
