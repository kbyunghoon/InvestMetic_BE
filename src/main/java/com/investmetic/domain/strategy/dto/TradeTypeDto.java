package com.investmetic.domain.strategy.dto;

import com.investmetic.domain.strategy.model.entity.TradeType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeTypeDto {
    private Long tradeTypeId;
    private String tradeTypeName;
    private String tradeIconUrl;

    // Entity -> DTO
    public static TradeTypeDto fromEntity(TradeType tradeType) {
        return new TradeTypeDto(
                tradeType.getTradeTypeId(),
                tradeType.getTradeTypeName(),
                tradeType.getTradeIconURL()
        );
    }
}
