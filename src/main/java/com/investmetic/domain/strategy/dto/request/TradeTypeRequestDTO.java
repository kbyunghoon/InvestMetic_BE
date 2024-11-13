package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.TradeType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeTypeRequestDTO {
    private String tradeTypeName;

    @Column(length = 1000)
    private String tradeTypeIconURL;
    int size;

    @Builder
    public TradeTypeRequestDTO(String tradeName, Boolean activateState, String tradeTypeIconURL, int size) {
        this.tradeTypeName = tradeName;
        this.tradeTypeIconURL = tradeTypeIconURL;
        this.size = size;
    }

    public TradeType toEntity() {
        return TradeType.builder()
                .tradeTypeName(tradeTypeName)
                .activateState(true)
                .tradeIconURL(tradeTypeIconURL)
                .build();
    }

}
