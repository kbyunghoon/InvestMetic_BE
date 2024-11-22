package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class StrategyDailyAnalysisServiceTest {

    @Mock
    private DailyAnalysisRepository dailyAnalysisRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @InjectMocks
    private StrategyAnalysisService strategyAnalysisService;

    private Strategy strategy;


    @BeforeEach
    void setUp() {
        User user = TestEntityFactory.createTestUser();
        TradeType tradeType = TestEntityFactory.createTestTradeType();
        strategy = TestEntityFactory.createTestStrategy(user, tradeType);
    }

    @Test
    @DisplayName("새로운 DailyAnalysis가 성공적으로 생성되는지 테스트")
    void 테스트_1() {
        Long strategyId = 1L;
        LocalDate date = LocalDate.now();
        TraderDailyAnalysisRequestDto requestDto = TraderDailyAnalysisRequestDto.builder()
                .date(date)
                .transaction(100L)
                .dailyProfitLoss(100L)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        strategyAnalysisService.createDailyAnalysis(strategyId, List.of(requestDto));

        verify(dailyAnalysisRepository, times(1)).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략이 존재하지 않을 경우 테스트")
    void 테스트_2() {
        Long strategyId = 1L;
        LocalDate date = LocalDate.now();
        TraderDailyAnalysisRequestDto requestDto = TraderDailyAnalysisRequestDto.builder()
                .date(date)
                .transaction(100L)
                .dailyProfitLoss(200L)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        List<TraderDailyAnalysisRequestDto> requestList = List.of(requestDto);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> strategyAnalysisService.createDailyAnalysis(strategyId, requestList));

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("전략의 모든 일간 분석 데이터 삭제 - 성공")
    void 테스트_3() {
        Long strategyId = 1L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, times(1)).deleteAllByStrategy(strategy);
        verify(strategyRepository, never()).delete(strategy);
    }

    @Test
    @DisplayName("전략의 모든 일간 분석 데이터 삭제 - 실패 (전략이 존재하지 않음)")
    void 테스트_4() {
        Long strategyId = 999L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, never()).deleteAllByStrategy(any(Strategy.class));
    }

    @Test
    @DisplayName("전략의 모든 일간 분석 데이터 삭제 - 성공 (전략 초기화 확인)")
    void 테스트_5() {
        Long strategyId = 1L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, times(1)).deleteAllByStrategy(strategy);

        assertEquals(0.0, strategy.getKpRatio());
        assertEquals(0.0, strategy.getSmScore());
        assertEquals(0.0, strategy.getZScore());
    }
}
