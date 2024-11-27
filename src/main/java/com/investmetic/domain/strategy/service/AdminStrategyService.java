package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStrategyService {
    private final StrategyRepository strategyRepository;

    @Transactional
    public void manageAproveState(Long strategyId, IsApproved isApproved) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        // Fixme : 권한 체크 로직 추가 예정
        strategy.setIsApproved(isApproved);
    }
}
