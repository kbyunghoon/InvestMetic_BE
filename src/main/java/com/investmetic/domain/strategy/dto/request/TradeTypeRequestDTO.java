package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.TradeType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeTypeRequestDTO {
    private String tradeName;

    @Column(length = 1000)
    private String tradeIconURL;

    @Builder
    public TradeTypeRequestDTO( String tradeName, Boolean activateState, String tradeIconURL) {
        this.tradeName = tradeName;
        this.tradeIconURL = tradeIconURL;
    }

    public TradeType toEntity() {
        return TradeType.builder()
                .tradeName(tradeName)
                .activateState(true)
                .tradeIconURL(tradeIconURL)
                .build();
    }

}
