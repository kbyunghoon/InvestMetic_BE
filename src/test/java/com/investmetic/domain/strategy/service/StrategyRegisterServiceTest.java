package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
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
class StrategyRegisterServiceTest {

    @InjectMocks
    private StrategyRegisterService strategyRegisterService;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private StockTypeRepository stockTypeRepository;

    @Mock
    private StockTypeGroupRepository stockTypeGroupRepository;

    @Mock
    private UserRepository userRepository;

    private StrategyRegisterRequestDto requestDto;
    private User user;
    private Strategy strategy;

    private List<TradeType> tradeTypeList;
    private List<StockType> stockTypeList;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

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
    @DisplayName("전략 등록 테스트")
    void 테스트_1() {

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

        PresignedUrlResponseDto responseDto = strategyRegisterService.registerStrategy(requestDto);

        assertEquals(presignedUrl, responseDto.getPresignedUrl());
    }

    @Test
    @DisplayName("유저 검증 테스트")
    void 테스트_2() {

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(BusinessException.class, () -> strategyRegisterService.registerStrategy(requestDto),
                ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("TradeType과 StockType 목록 요청 테스트")
    void 테스트_3() {

        when(tradeTypeRepository.findByActivateStateTrue()).thenReturn(tradeTypeList);
        when(stockTypeRepository.findAll()).thenReturn(stockTypeList);

        ResponseEntity<BaseResponse<RegisterInfoResponseDto>> response = BaseResponse.success(
                strategyRegisterService.loadStrategyRegistrationInfo());

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
    @DisplayName("전략 수정 성공")
    void 테스트_4() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .proposalFile(
                        ProposalFileDto.builder()
                                .proposalFileName("proposal.xls")
                                .proposalFileSize(1024)
                                .build()
                )
                .description("Updated description")
                .build();

        String proposalFilePath = "strategies/proposals/proposal.xls";
        String presignedUrl = "https://s3.amazonaws.com/test-bucket/proposal.xls";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(strategyRepository.findById(anyLong())).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(FilePath.STRATEGY_PROPOSAL, "proposal.xls", 1024))
                .thenReturn(proposalFilePath);
        when(s3FileService.getPreSignedUrl(proposalFilePath)).thenReturn(presignedUrl);

        PresignedUrlResponseDto responseDto = strategyRegisterService.modifyStrategy(1L, requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getPresignedUrl()).isEqualTo(presignedUrl);

        verify(strategyRepository).findById(1L);
    }

    @Test
    @DisplayName("전략 수정 실패 - 전략 미존재")
    void 테스트_5() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .proposalFile(
                        ProposalFileDto.builder()
                                .proposalFileName("proposal.pdf")
                                .proposalFileSize(1024)
                                .build()
                )
                .description("Updated description")
                .build();

        User user = TestEntityFactory.createTestUser();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(strategyRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyRegisterService.modifyStrategy(1L, requestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STRATEGY_NOT_FOUND);

        verify(strategyRepository).findById(1L);
    }

    @Test
    @DisplayName("전략 수정 실패 - 사용자 미존재")
    void 테스트_6() {
        StrategyModifyRequestDto requestDto = StrategyModifyRequestDto.builder()
                .strategyName("Updated Strategy")
                .proposalFile(
                        ProposalFileDto.builder()
                                .proposalFileName("proposal.pdf")
                                .proposalFileSize(1024)
                                .build()
                )
                .description("Updated description")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> strategyRegisterService.modifyStrategy(1L, requestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);

        verify(userRepository).findById(1L);
        verify(strategyRepository, never()).findById(anyLong());
    }
}