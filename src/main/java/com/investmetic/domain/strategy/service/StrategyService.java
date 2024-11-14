package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.exception.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final TradeTypeRepository tradeTypeRepository;
    private final StockTypeRepository stockTypeRepository;

    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {
        List<TradeTypeDto> tradeTypesDto = getActiveTradeTypes();
        List<StockTypeDto> stockTypesDto = getAllStockTypes();

        RegisterInfoResponseDto responseDto = buildRegisterInfoResponse(tradeTypesDto, stockTypesDto);

        return BaseResponse.success(responseDto);
    }

    private List<TradeTypeDto> getActiveTradeTypes() {
        return tradeTypeRepository.findByActivateStateTrue().stream()
                .map(TradeTypeDto::fromEntity)
                .toList();
    }

    private List<StockTypeDto> getAllStockTypes() {
        return stockTypeRepository.findAll().stream()
                .map(StockTypeDto::from)
                .toList();
    }

    private RegisterInfoResponseDto buildRegisterInfoResponse(
            List<TradeTypeDto> tradeTypes,
            List<StockTypeDto> stockTypes
    ) {
        return RegisterInfoResponseDto.builder()
                .tradeTypes(tradeTypes)
                .stockTypes(stockTypes)
                .build();
    }
}
