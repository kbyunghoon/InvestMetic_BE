package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.TradeType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchInfoResponseDto {
    private final List<String> stockTypeNames;
    private final List<String> tradeTypeNames;

    public static SearchInfoResponseDto from(List<StockType> stockTypeNames, List<TradeType> tradeTypeNames) {
        return new SearchInfoResponseDto(
                stockTypeNames.stream()
                        .map(StockType::getStockTypeName)
                        .toList(),
                tradeTypeNames.stream()
                        .map(TradeType::getTradeTypeName)
                        .toList()
        );
    }
}
