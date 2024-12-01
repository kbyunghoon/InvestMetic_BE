package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
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
import org.mockito.Mockito;
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

    @Mock
    private Strategy strategy;

    @Mock
    private TraderDailyAnalysisRequestDto requestDailyAnalysis;

    @Mock
    private DailyAnalysis existingDailyAnalysisProceedYes;

    @Mock
    private DailyAnalysis existingDailyAnalysisProceedNo;

    @Mock
    private User user;

    private LocalDate date;


    @BeforeEach
    void setUp() {
        date = LocalDate.now();
        user = User.builder()
                .userId(1L)
                .userName("testUser")
                .nickname("Test Nickname")
                .email("testuser@example.com")
                .password("encryptedPassword")
                .imageUrl("http://example.com/image.jpg")
                .phone("123-456-7890")
                .birthDate("19900101")
                .ipAddress("192.168.0.1")
                .infoAgreement(true)
                .joinDate(LocalDate.now())
                .withdrawalDate(null)
                .userState(UserState.ACTIVE)
                .withdrawalStatus(false)
                .role(Role.INVESTOR)
                .build();

        TradeType tradeType = TestEntityFactory.createTestTradeType();

        strategy = TestEntityFactory.createTestStrategy(user, tradeType);

        requestDailyAnalysis = TraderDailyAnalysisRequestDto.builder()
                .date(LocalDate.now())
                .transaction(5000L)
                .dailyProfitLoss(1000L)
                .build();

        existingDailyAnalysisProceedYes = spy(DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(date)
                .transaction(100L)
                .dailyProfitLoss(100L)
                .proceed(Proceed.YES)
                .build());

        existingDailyAnalysisProceedNo = spy(DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(date)
                .transaction(200L)
                .dailyProfitLoss(200L)
                .proceed(Proceed.NO)
                .build());
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 성공 테스트")
    void 전략_일간_분석_등록_테스트_1() {
        when(strategyRepository.findById(strategy.getStrategyId())).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, requestDailyAnalysis.getDate()))
                .thenReturn(Optional.empty());

        strategyAnalysisService.createDailyAnalysis(strategy.getStrategyId(), List.of(requestDailyAnalysis),
                user.getUserId());

        verify(dailyAnalysisRepository, times(1)).save(argThat(dailyAnalysis ->
                dailyAnalysis.getStrategy().equals(strategy) &&
                        dailyAnalysis.getDailyDate().equals(requestDailyAnalysis.getDate()) &&
                        dailyAnalysis.getTransaction().equals(requestDailyAnalysis.getTransaction()) &&
                        dailyAnalysis.getDailyProfitLoss().equals(requestDailyAnalysis.getDailyProfitLoss()) &&
                        dailyAnalysis.getProceed() == Proceed.NO
        ));
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 전략이 존재하지 않을 경우")
    void 전략_일간_분석_등록_테스트_2() {
        Long strategyId = strategy.getStrategyId();
        List<TraderDailyAnalysisRequestDto> dailyAnalysisList = List.of(requestDailyAnalysis);
        Long userId = user.getUserId();

        when(strategyRepository.findById(strategy.getStrategyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyAnalysisService.createDailyAnalysis(strategyId, dailyAnalysisList,
                        userId));

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(dailyAnalysisRepository, never()).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 이미 존재하는 경우 예외 발생")
    void 전략_일간_분석_등록_테스트_3() {
        Long strategyId = strategy.getStrategyId();
        List<TraderDailyAnalysisRequestDto> dailyAnalysisList = List.of(requestDailyAnalysis);
        Long userId = user.getUserId();

        when(strategyRepository.findById(strategy.getStrategyId())).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, requestDailyAnalysis.getDate()))
                .thenReturn(Optional.of(existingDailyAnalysisProceedNo));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyAnalysisService.createDailyAnalysis(strategyId, dailyAnalysisList,
                        userId));

        assertEquals(ErrorCode.DAILY_ANALYSIS_ALREADY_EXISTS, exception.getErrorCode());
        verify(dailyAnalysisRepository, never()).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략 일간 분석 등록 - 권한이 없는 사용자일 경우 예외 발생")
    void 전략_일간_분석_등록_테스트_4() {
        Long strategyId = strategy.getStrategyId();
        List<TraderDailyAnalysisRequestDto> dailyAnalysisList = List.of(requestDailyAnalysis);

        when(strategyRepository.findById(strategy.getStrategyId())).thenReturn(Optional.of(strategy));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyAnalysisService.createDailyAnalysis(strategyId, dailyAnalysisList,
                        2L)); // 다른 userId

        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
        verify(dailyAnalysisRepository, never()).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략 모든 일간 분석 전체 삭제 - 성공 테스트")
    void 전략_모든_일간_분석_전체_삭제_테스트_1() {
        Long strategyId = strategy.getStrategyId();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, times(1)).deleteAllByStrategy(strategy);
        verify(strategyRepository, never()).delete(strategy);
    }

    @Test
    @DisplayName("전략 모든 일간 분석 전체 삭제 - 전략이 존재하지 않을 경우")
    void 전략_모든_일간_분석_전체_삭제_테스트_2() {
        Long strategyId = strategy.getStrategyId();

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
    void 전략_모든_일간_분석_전체_삭제_테스트_3() {
        Long strategyId = strategy.getStrategyId();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

        strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId);

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(dailyAnalysisRepository, times(1)).deleteAllByStrategy(strategy);

        assertEquals(0.0, strategy.getKpRatio());
        assertEquals(0.0, strategy.getSmScore());
    }


    @Test
    @DisplayName("전략 일간 분석 수정 - 진행 상태가 YES인 경우 새로운 데이터 저장")
    void 전략_일간_분석_수정_테스트_1() {
        Long strategyId = strategy.getStrategyId();

        Mockito.when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        Mockito.when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, date))
                .thenReturn(Optional.of(existingDailyAnalysisProceedYes));

        strategyAnalysisService.modifyDailyAnalysis(strategyId, requestDailyAnalysis, user.getUserId());

        verify(dailyAnalysisRepository, times(1)).save(argThat(newData ->
                newData.getStrategy().equals(strategy) &&
                        newData.getDailyDate().equals(date) &&
                        newData.getTransaction().equals(requestDailyAnalysis.getTransaction()) &&
                        newData.getDailyProfitLoss().equals(requestDailyAnalysis.getDailyProfitLoss()) &&
                        newData.getProceed() == Proceed.NO
        ));
    }

    @Test
    @DisplayName("일간 분석 수정 - 진행 상태가 NO인 경우 기존 데이터 수정")
    void 전략_일간_분석_수정_테스트_2() {
        Long strategyId = strategy.getStrategyId();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, date))
                .thenReturn(Optional.of(existingDailyAnalysisProceedNo));

        doNothing().when(existingDailyAnalysisProceedNo)
                .modifyDailyAnalysis(requestDailyAnalysis.getTransaction(), requestDailyAnalysis.getDailyProfitLoss());

        strategyAnalysisService.modifyDailyAnalysis(strategyId, requestDailyAnalysis, user.getUserId());

        verify(existingDailyAnalysisProceedNo, times(1))
                .modifyDailyAnalysis(requestDailyAnalysis.getTransaction(), requestDailyAnalysis.getDailyProfitLoss());
        verify(dailyAnalysisRepository, never()).save(any(DailyAnalysis.class));
    }

    @Test
    @DisplayName("전략 일간 분석 수정 - 전략 찾을 수 없을 경우")
    void 전략_일간_분석_수정_테스트_3() {
        Long strategyId = strategy.getStrategyId();
        Long userId = user.getUserId();

        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyAnalysisService.modifyDailyAnalysis(strategyId, requestDailyAnalysis, userId)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository).findById(1L);
        verify(dailyAnalysisRepository, never()).findDailyAnalysisByStrategyAndDate(any(), any());
    }

    @Test
    @DisplayName("전략 일간 분석 수정 - 해당 일간 분석이 존재하지 않을 경우")
    void 전략_일간_분석_수정_테스트_4() {
        Long strategyId = strategy.getStrategyId();
        Long userId = user.getUserId();

        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyAnalysisService.modifyDailyAnalysis(strategyId, requestDailyAnalysis, userId)
        );

        assertEquals(ErrorCode.DAILY_ANALYSIS_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository).findById(1L);
        verify(dailyAnalysisRepository).findDailyAnalysisByStrategyAndDate(strategy, LocalDate.now());
    }


    @Test
    @DisplayName("전략 일간 분석 삭제 - 성공 테스트")
    void 전략_일간_분석_단일_삭제_테스트_1() {
        Long strategyId = strategy.getStrategyId();
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
    void 전략_일간_분석_단일_삭제_테스트_2() {
        Long strategyId = strategy.getStrategyId();
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
