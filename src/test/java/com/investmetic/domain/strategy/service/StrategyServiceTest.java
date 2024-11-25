package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.investmetic.domain.strategy.dto.response.StrategyModifyInfoResponseDto;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class StrategyServiceTest {

    @InjectMocks
    private StrategyService strategyService;

    @InjectMocks
    private StrategyRegisterService strategyRegisterService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private StockTypeGroupRepository stockTypeGroupRepository;

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

    @Test
    @DisplayName("전략 삭제 - 성공")
    void 테스트_4() {
        Long strategyId = 1L;
        Strategy strategy = Strategy.builder()
                .strategyId(strategyId)
                .isPublic(IsPublic.PUBLIC)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyService.deleteStrategy(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(strategyRepository, times(1)).deleteById(strategyId);
    }

    @Test
    @DisplayName("전략 삭제 - 실패 (전략 ID가 존재하지 않을 때)")
    void 테스트_5() {
        Long strategyId = 999L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyService.deleteStrategy(strategyId)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, times(1)).findById(strategyId);
        verify(strategyRepository, never()).deleteById(strategyId);
    }

    @Test
    @DisplayName("전략 수정 - 전략 수정 페이지 진입 시 수정할 전략 데이터 조회")
    void 테스트_6() {
        Long strategyId = 1L;
        Strategy strategy = Strategy.builder()
                .strategyId(strategyId)
                .strategyName("Test Strategy")
                .tradeType(TradeType.builder()
                        .tradeTypeId(1L)
                        .tradeTypeName("Trade Type Name")
                        .build())
                .operationCycle(OperationCycle.POSITION)
                .minimumInvestmentAmount(MinimumInvestmentAmount.FROM_5M_TO_10M)
                .proposalFilePath("path/to/proposal")
                .build();

        List<StockType> stockTypes = List.of(
                StockType.builder().stockTypeId(1L).stockTypeName("Stock Type 1").build(),
                StockType.builder().stockTypeId(2L).stockTypeName("Stock Type 2").build()
        );

        Mockito.when(strategyRepository.findById(strategyId))
                .thenReturn(Optional.of(strategy));
        Mockito.when(stockTypeGroupRepository.findStockTypeIdsByStrategy(strategy))
                .thenReturn(stockTypes);

        StrategyModifyInfoResponseDto result = strategyRegisterService.loadStrategyModifyInfo(strategyId);

        assertNotNull(result);
        assertEquals("Test Strategy", result.getStrategyName());
        assertEquals(1L, result.getTradeTypeId());
        assertEquals(OperationCycle.POSITION, result.getOperationCycle());
        assertEquals(List.of(1L, 2L), result.getStockTypeIds());
        assertEquals(MinimumInvestmentAmount.FROM_5M_TO_10M, result.getMinimumInvestmentAmount());
        assertEquals("path/to/proposal", result.getProposalFileUrl());

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(stockTypeGroupRepository, times(1)).findStockTypeIdsByStrategy(strategy);
    }

    @Test
    @DisplayName("전략 수정 - 수정할 전략 데이터 조회 시 전략을 찾을 수 없을 경우")
    void 테스트_7() {
        Long strategyId = 1L;

        Mockito.when(strategyRepository.findById(strategyId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyRegisterService.loadStrategyModifyInfo(strategyId)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, times(1)).findById(strategyId);
        verifyNoInteractions(stockTypeGroupRepository);
    }
}
