package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.dto.object.StockTypeDto;
import com.investmetic.domain.strategy.dto.object.TradeTypeDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterInfoResponseDto {
    private List<StockTypeDto> stockTypes;
    private List<TradeTypeDto> tradeTypes;

    @Builder
    public RegisterInfoResponseDto(List<StockTypeDto> stockTypes, List<TradeTypeDto> tradeTypes) {
        this.stockTypes = stockTypes;
        this.tradeTypes = tradeTypes;
    }
}
