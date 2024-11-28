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
import com.investmetic.domain.strategy.model.entity.Proceed;
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
    private DailyAnalysis dailyAnalysis;
    private TraderDailyAnalysisRequestDto requestDto;

    @BeforeEach
    void setUp() {
        User user = TestEntityFactory.createTestUser();

        TradeType tradeType = TestEntityFactory.createTestTradeType();

        strategy = TestEntityFactory.createTestStrategy(user, tradeType);

        dailyAnalysis = DailyAnalysis.builder()
                .dailyAnalysisId(1L)
                .strategy(strategy)
                .dailyDate(LocalDate.of(2024, 1, 1))
                .transaction(100000L)
                .dailyProfitLoss(5000L)
                .proceed(Proceed.YES)
                .build();

        requestDto = TraderDailyAnalysisRequestDto.builder()
                .date(LocalDate.now())
                .transaction(5000L)
                .dailyProfitLoss(1000L)
                .build();
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 성공 테스트")
    void 테스트_1() {
        Long strategyId = 1L;
        LocalDate date = LocalDate.now();
        TraderDailyAnalysisRequestDto traderDailyAnalysisRequestDto = TraderDailyAnalysisRequestDto.builder()
                .date(date)
                .transaction(100L)
                .dailyProfitLoss(100L)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        strategyAnalysisService.createDailyAnalysis(strategyId, List.of(traderDailyAnalysisRequestDto));

        verify(dailyAnalysisRepository, times(1)).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 전략이 존재하지 않을 경우")
    void 테스트_2() {
        Long strategyId = 1L;
        LocalDate date = LocalDate.now();
        TraderDailyAnalysisRequestDto traderDailyAnalysisRequestDto = TraderDailyAnalysisRequestDto.builder()
                .date(date)
                .transaction(100L)
                .dailyProfitLoss(200L)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        List<TraderDailyAnalysisRequestDto> requestList = List.of(traderDailyAnalysisRequestDto);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> strategyAnalysisService.createDailyAnalysis(strategyId, requestList));

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("전략 모든 일간 분석 전체 삭제 - 성공 테스트")
    void 테스트_3() {
        Long strategyId = 1L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, times(1)).deleteAllByStrategy(strategy);
        verify(strategyRepository, never()).delete(strategy);
    }

    @Test
    @DisplayName("전략 모든 일간 분석 전체 삭제 - 전략이 존재하지 않을 경우")
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
    @DisplayName("전략 모든 일간 분석 전체 삭제 - 성공 (전략 초기화 확인)")
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


    @Test
    @DisplayName("전략 일간 분석 수정 - 성공 테스트")
    void 테스트_6() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now()))
                .thenReturn(Optional.of(dailyAnalysis));

        strategyAnalysisService.modifyDailyAnalysis(1L, requestDto);

        assertEquals(5000L, dailyAnalysis.getTransaction());
        assertEquals(1000L, dailyAnalysis.getDailyProfitLoss());
        verify(strategyRepository).findById(1L);
        verify(dailyAnalysisRepository).findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now());
    }

    @Test
    @DisplayName("전략 일간 분석 수정 - 전략 찾을 수 없을 경우")
    void 테스트_7() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyAnalysisService.modifyDailyAnalysis(1L, requestDto)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository).findById(1L);
        verify(dailyAnalysisRepository, never()).findDailyAnalysisByStrategyAndDate(any(), any());
    }

    @Test
    @DisplayName("전략 일간 분석 수정 - 해당 일간 분석이 존재하지 않을 경우")
    void 테스트_8() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyAnalysisService.modifyDailyAnalysis(1L, requestDto)
        );

        assertEquals(ErrorCode.DAILY_ANALYSIS_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository).findById(1L);
        verify(dailyAnalysisRepository).findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now());
    }


    @Test
    @DisplayName("전략 일간 분석 삭제 - 성공 테스트")
    void 테스트_9() {
        Long strategyId = 1L;
        Long analysisId = 123L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.existsByStrategyAndDailyAnalysisId(strategy, analysisId))
                .thenReturn(true);

        strategyAnalysisService.deleteStrategyDailyAnalysis(strategyId, analysisId);

        verify(dailyAnalysisRepository, times(1))
                .deleteByStrategyAndDailyAnalysisId(strategy, analysisId);
    }

    @Test
    @DisplayName("전략 일간 분석 삭제 - 해당 일간 분석이 존재하지 않을 경우 예외 발생")
    void 테스트_10() {
        Long strategyId = 1L;
        Long analysisId = 123L;

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.existsByStrategyAndDailyAnalysisId(strategy, analysisId))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyAnalysisService.deleteStrategyDailyAnalysis(strategyId, analysisId)
        );

        assertEquals(ErrorCode.INVALID_TYPE_VALUE, exception.getErrorCode());
        verify(dailyAnalysisRepository, never())
                .deleteByStrategyAndDailyAnalysisId(strategy, analysisId);
    }
}
