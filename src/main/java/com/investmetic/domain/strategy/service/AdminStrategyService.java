package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.AdminStrategyResponseDto;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStrategyService {
    private final StrategyRepository strategyRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;

    @Transactional
    public void manageApproveState(Long strategyId, IsApproved isApproved) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findByStrategy(strategy);

        if (dailyAnalyses.size() >= 3 && isApproved.equals(IsApproved.APPROVED)) {
            strategy.setIsPublic(IsPublic.PUBLIC);
        }

        strategy.setIsApproved(isApproved);
    }

    @Transactional
    public PageResponseDto<AdminStrategyResponseDto> getManageStrategies(Pageable pageable, String searchWord,
                                                                         IsApproved isApproved) {
        return new PageResponseDto<>(strategyRepository.findAdminStrategies(pageable, searchWord, isApproved));
    }
}
