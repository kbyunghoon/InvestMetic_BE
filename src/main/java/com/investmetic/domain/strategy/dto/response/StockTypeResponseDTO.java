package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.entity.StockType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockTypeResponseDTO {
    private Long stockTypeId;

    private String stockTypeName; // 종목명
    private Boolean activateState; // 종목 활성 상태

    @Column(length = 1000)
    private String stockTypeIconURL;
    @Builder
    public StockTypeResponseDTO(Long stockTypeId, String stockTypeName, Boolean activateState, String stockTypeIconURL) {
        this.stockTypeId = stockTypeId;
        this.stockTypeName = stockTypeName;
        this.activateState = activateState;
        this.stockTypeIconURL = stockTypeIconURL;
    }

    public static StockTypeResponseDTO from(StockType stockType){
        return StockTypeResponseDTO.builder()
                .stockTypeId(stockType.getStockTypeId())
                .stockTypeName(stockType.getStockTypeName())
                .activateState(stockType.getActivateState())
                .stockTypeIconURL(stockType.getStockTypeIconURL())
                .build();
    }
}
