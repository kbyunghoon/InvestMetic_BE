package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final StrategyRepository strategyRepository;

    @Transactional
    public void updateVisibility(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // FIXME : 권한 체크 로직 추가 예정
//        if (strategy.getCreatedBy() != user) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
//        }

        Strategy updatedStrategy = strategy.toBuilder()
                .isPublic(strategy.getIsPublic() == IsPublic.PUBLIC
                        ? IsPublic.PRIVATE
                        : IsPublic.PUBLIC)
                .build();

        strategyRepository.save(updatedStrategy);
    }
}