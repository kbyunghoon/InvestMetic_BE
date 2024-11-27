package com.investmetic.domain.strategy.dto;

import com.investmetic.domain.strategy.model.entity.StockType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StockTypeDto {
    private Long stockTypeId;
    private String stockTypeName;
    private String stockIconUrl;


    @Builder
    public StockTypeDto(Long stockTypeId, String stockTypeName, String stockIconUrl) {
        this.stockTypeId = stockTypeId;
        this.stockTypeName = stockTypeName;
        this.stockIconUrl = stockIconUrl;
    }

    // Entity -> DTO 변환
    public static StockTypeDto from(StockType stockType) {
        return StockTypeDto.builder()
                .stockTypeId(stockType.getStockTypeId())
                .stockTypeName(stockType.getStockTypeName())
                .stockIconUrl(stockType.getStockTypeIconUrl())
                .build();
    }
}
