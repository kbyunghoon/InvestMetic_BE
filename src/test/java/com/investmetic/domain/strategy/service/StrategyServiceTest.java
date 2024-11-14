package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.exception.BaseResponse;
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
class StrategyServiceTest {

    @InjectMocks
    private StrategyService strategyService;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private StockTypeRepository stockTypeRepository;

    private List<TradeType> tradeTypeList;
    private List<StockType> stockTypeList;

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
    }

    @Test
    @DisplayName("loadStrategyRegistrationInfo - 성공적으로 TradeType과 StockType 목록을 가져오는지 테스트")
    void loadStrategyRegistrationInfo() {
        when(tradeTypeRepository.findByActivateStateTrue()).thenReturn(tradeTypeList);
        when(stockTypeRepository.findAll()).thenReturn(stockTypeList);

        ResponseEntity<BaseResponse<RegisterInfoResponseDto>> response = strategyService.loadStrategyRegistrationInfo();

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
