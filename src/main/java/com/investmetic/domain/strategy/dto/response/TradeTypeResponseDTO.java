package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.entity.TradeType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeTypeResponseDTO {
    private Long tradeTypeId;
    private String tradeName;
    private Boolean activateState;

    @Column(length = 1000)
    private String tradeIconURL;

    @Builder
    public TradeTypeResponseDTO(Long tradeTypeId, String tradeName, Boolean activateState, String tradeIconURL) {
        this.tradeTypeId = tradeTypeId;
        this.tradeName = tradeName;
        this.activateState = activateState;
        this.tradeIconURL = tradeIconURL;
    }

    public TradeTypeResponseDTO from(TradeType tradeType) {
        return TradeTypeResponseDTO.builder()
                .tradeTypeId(tradeType.getTradeTypeId())
                .tradeName(tradeType.getTradeTypeName())
                .tradeIconURL(tradeType.getTradeTypeIconURL())
                .activateState(tradeType.getActivateState())
                .build();
    }
}
