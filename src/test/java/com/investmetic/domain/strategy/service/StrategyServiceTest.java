package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StrategyServiceTest {

    @InjectMocks
    private StrategyService strategyService;

    @Mock
    private StrategyRepository strategyRepository;

    @Test
    @DisplayName("공개 여부 변경 - 성공 (공개 -> 비공개)")
    void 테스트_1() {
        Long strategyId = 1L;
        Strategy strategy = Strategy.builder()
                .strategyId(strategyId)
                .isPublic(IsPublic.PUBLIC)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyService.updateVisibility(strategyId);

        assertEquals(IsPublic.PRIVATE, strategy.getIsPublic());
    }

    @Test
    @DisplayName("공개 여부 변경 - 성공 (비공개 -> 공개)")
    void 테스트_2() {
        Long strategyId = 1L;
        Strategy strategy = Strategy.builder()
                .strategyId(strategyId)
                .isPublic(IsPublic.PRIVATE)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyService.updateVisibility(strategyId);

        assertEquals(IsPublic.PUBLIC, strategy.getIsPublic());
    }

    @Test
    @DisplayName("공개 여부 변경 - 실패 (전략 ID가 존재하지 않음)")
    void 테스트_3() {
        Long strategyId = 999L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyService.updateVisibility(strategyId)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, never()).save(any());
    }
}
