package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StrategyRegisterServiceTest {

    @InjectMocks
    private StrategyRegisterService strategyRegisterService;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private StockTypeRepository stockTypeRepository;

    @Mock
    private UserRepository userRepository;

    private StrategyRegisterRequestDto requestDto;
    private User user;

    private List<TradeType> tradeTypeList;
    private List<StockType> stockTypeList;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

    @BeforeEach
    void setUp() {
        // Mock 데이터 생성
        tradeTypeList = List.of(
                new TradeType(1L, "TradeType1", true, "https://example.com/TradeType1.png"),
                new TradeType(2L, "TradeType2", true, "https://example.com/TradeType2.png")
        );

        stockTypeList = List.of(
                new StockType(1L, "StockType1", true, "https://example.com/StockType1.png"),
                new StockType(2L, "StockType2", true, "https://example.com/StockType2.png")
        );

        // 테스트용 유저 생성
        user = User.builder()
                .userName("testUser")
                .nickname("Tester")
                .email("test@example.com")
                .build();

        // 테스트용 요청 DTO 생성
        requestDto = StrategyRegisterRequestDto.builder()
                .strategyName("Test Strategy")
                .tradeTypeId(1L)
                .operationCycle(OperationCycle.DAY)
                .minimumInvestmentAmount(MinimumInvestmentAmount.ABOVE_100M)
                .proposalFile(ProposalFileDto.builder().proposalFileName("test.xls").proposalFileSize(1024).build())
                .build();
    }

    @Test
    @DisplayName("전략 등록 테스트")
    void registerStrategySuccessTest() {
        // given
        TradeType tradeType = TradeType.builder().tradeTypeId(1L).tradeTypeName("Swing Trading").build();
        String presignedUrl = "https://s3.amazonaws.com/test-bucket/test.pdf";
        String proposalFilePath = "strategies/proposals/test.pdf";

        // Mocking
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));
        when(tradeTypeRepository.findByTradeTypeId(anyLong())).thenReturn(java.util.Optional.of(tradeType));
        when(s3FileService.getS3Path(FilePath.STRATEGY_PROPOSAL, "test.xls", 1024)).thenReturn(proposalFilePath);
        when(s3FileService.getPreSignedUrl(proposalFilePath)).thenReturn(presignedUrl);
        when(strategyRepository.save(any(Strategy.class))).thenReturn(Strategy.builder().build());

        // when
        PresignedUrlResponseDto responseDto = strategyRegisterService.registerStrategy(requestDto);

        // then
        assertEquals(presignedUrl, responseDto.getPresignedUrl());
    }

    @Test
    @DisplayName("유저 검증 테스트")
    void notFoundUserTest() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> strategyRegisterService.registerStrategy(requestDto),
                ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("TradeType과 StockType 목록 요청 테스트")
    void loadStrategyRegistrationInfoTest() {
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
}