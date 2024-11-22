package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
}
