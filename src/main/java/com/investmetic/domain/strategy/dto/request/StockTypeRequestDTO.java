package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.StockType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockTypeRequestDTO {
    private String stockTypeName; // 종목명

    @Column(length = 1000)
    private String stockTypeIconUrl; // 종목아이콘 경로

    private int size;


    @Builder
    public StockTypeRequestDTO(String stockTypeName, String stockTypeIconUrl, int size) {
        this.stockTypeName = stockTypeName;
        this.stockTypeIconUrl = stockTypeIconUrl;
        this.size = size;
    }

    public StockType toEntity() {

        return StockType.builder()
                .stockTypeName(stockTypeName)
                .activateState(true)
                .stockTypeIconUrl(stockTypeIconUrl).build();
    }

}
