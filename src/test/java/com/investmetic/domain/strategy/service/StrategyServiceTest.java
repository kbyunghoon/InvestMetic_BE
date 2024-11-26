package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.StockTypeGroup;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.dto.ProposalFileDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class StrategyServiceTest {

    @InjectMocks
    private StrategyService strategyService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private StockTypeGroupRepository stockTypeGroupRepository;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private StockTypeRepository stockTypeRepository;

    @Mock
    private UserRepository userRepository;

    private StrategyRegisterRequestDto requestDto;
    private User user;
    private Strategy strategy;
    private List<TradeType> tradeTypeList;
    private List<StockType> stockTypeList;

    @BeforeEach
    void setUp() {

        tradeTypeList = List.of(
                TradeType.builder().tradeTypeId(1L).tradeTypeName("TradeType1").activateState(true)
                        .tradeTypeIconURL("https://example.com/TradeType1.png").build(),
                TradeType.builder().tradeTypeId(2L).tradeTypeName("TradeType2").activateState(true)
                        .tradeTypeIconURL("https://example.com/TradeType2.png").build()
        );

        stockTypeList = List.of(
                StockType.builder().stockTypeId(1L).stockTypeName("StockType1").activateState(true)
                        .stockTypeIconURL("https://example.com/StockType1.png").build(),
                StockType.builder().stockTypeId(2L).stockTypeName("StockType2").activateState(true)
                        .stockTypeIconURL("https://example.com/StockType2.png").build()
        );

        user = User.builder()
                .userName("testUser")
                .nickname("Tester")
                .email("test@example.com")
                .build();

        requestDto = StrategyRegisterRequestDto.builder()
                .strategyName("Test Strategy")
                .tradeTypeId(1L)
                .operationCycle(OperationCycle.DAY)
                .stockTypeIds(List.of(1L, 2L))
                .minimumInvestmentAmount(MinimumInvestmentAmount.ABOVE_100M)
                .proposalFile(ProposalFileDto.builder()
                        .proposalFileName("test.xls")
                        .proposalFileSize(1024)
                        .build())
                .build();

        TradeType tradeType = TestEntityFactory.createTestTradeType();

        strategy = TestEntityFactory.createTestStrategy(user, tradeType);
    }

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
    @DisplayName("전략 등록 - 성공")
    void 테스트_6() {

        TradeType tradeType = tradeTypeList.get(0);
        String presignedUrl = "https://s3.amazonaws.com/test-bucket/test.xls";
        String proposalFilePath = "strategies/proposals/test.xls";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tradeTypeRepository.findByTradeTypeId(anyLong())).thenReturn(Optional.of(tradeType));
        when(s3FileService.getS3Path(FilePath.STRATEGY_PROPOSAL, "test.xls", 1024)).thenReturn(proposalFilePath);
        when(s3FileService.getPreSignedUrl(proposalFilePath)).thenReturn(presignedUrl);
        when(stockTypeRepository.findById(anyLong()))
                .thenAnswer(invocation -> stockTypeList.stream()
                        .filter(stockType -> stockType.getStockTypeId().equals(invocation.getArgument(0)))
                        .findFirst());
        when(strategyRepository.save(any(Strategy.class))).thenReturn(Strategy.builder().build());
        when(stockTypeGroupRepository.save(any(StockTypeGroup.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PresignedUrlResponseDto responseDto = strategyService.registerStrategy(requestDto);

        assertEquals(presignedUrl, responseDto.getPresignedUrl());
    }

    @Test
    @DisplayName("전략 성공 - 유저 검증 테스트")
    void 테스트_7() {

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(BusinessException.class, () -> strategyService.registerStrategy(requestDto),
                ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전략 관련 - TradeType과 StockType 목록 요청 테스트")
    void 테스트_8() {

        when(tradeTypeRepository.findByActivateStateTrue()).thenReturn(tradeTypeList);
        when(stockTypeRepository.findAll()).thenReturn(stockTypeList);

        ResponseEntity<BaseResponse<RegisterInfoResponseDto>> response = BaseResponse.success(
                strategyService.loadStrategyRegistrationInfo());

        assertThat(response).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getIsSuccess()).isTrue();

        RegisterInfoResponseDto responseDto = response.getBody().getResult();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTradeTypes()).hasSize(2);
        assertThat(responseDto.getStockTypes()).hasSize(2);

        TradeTypeDto tradeTypeDto = responseDto.getTradeTypes().get(0);
        assertThat(tradeTypeDto.getTradeTypeName()).isEqualTo("TradeType1");
        assertThat(tradeTypeDto.getTradeTypeIconURL()).isEqualTo("https://example.com/TradeType1.png");

        StockTypeDto stockTypeDto = responseDto.getStockTypes().get(0);
        assertThat(stockTypeDto.getStockTypeName()).isEqualTo("StockType1");
        assertThat(stockTypeDto.getStockIconUrl()).isEqualTo("https://example.com/StockType1.png");
    }


    @Test
    @DisplayName("전략 수정 - 제안서 미변경 시 성공 테스트")
    void 테스트_9() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .description("Updated Description")
                .proposalModified(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));

        PresignedUrlResponseDto response = strategyService.modifyStrategy(1L, requestDto);

        assertNull(response);
        assertEquals("Updated Strategy", strategy.getStrategyName());
        assertEquals("Updated Description", strategy.getStrategyDescription());

        verify(strategyRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("전략 수정 - 제안서 수정 시 성공 테스트")
    void 테스트_10() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .description("Updated Description")
                .proposalModified(true)
                .proposalFile(ProposalFileDto.builder()
                        .proposalFileName("updated-file.pdf")
                        .proposalFileSize(1024)
                        .build())
                .build();

        String generatedFilePath = "strategies/proposals/updated-file.pdf";
        String presignedUrl = "https://s3.amazonaws.com/test-bucket/strategies/proposals/updated-file.pdf";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(FilePath.STRATEGY_PROPOSAL, "updated-file.pdf", 1024))
                .thenReturn(generatedFilePath);
        when(s3FileService.getPreSignedUrl(generatedFilePath)).thenReturn(presignedUrl);

        PresignedUrlResponseDto response = strategyService.modifyStrategy(1L, requestDto);

        assertNotNull(response);
        assertEquals(presignedUrl, response.getPresignedUrl());
        verify(s3FileService).getS3Path(FilePath.STRATEGY_PROPOSAL, "updated-file.pdf", 1024);
        verify(s3FileService).getPreSignedUrl(generatedFilePath);
    }

    @Test
    @DisplayName("전략 수정 - 전략이 존재하지 않을 경우 예외 발생 테스트")
    void 테스트_11() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .description("Updated Description")
                .proposalModified(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyService.modifyStrategy(1L, requestDto)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository).findById(1L);
    }
}
