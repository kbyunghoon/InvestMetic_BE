package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.entity.TradeType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeTypeResponseDTO {
    private Long tradeTypeId;
    private String tradeName;
    private Boolean activateState;

    @Column(length = 1000)
    private String tradeTypeIconUrl;

    @Builder
    public TradeTypeResponseDTO(Long tradeTypeId, String tradeName, Boolean activateState, String tradeTypeIconUrl) {
        this.tradeTypeId = tradeTypeId;
        this.tradeName = tradeName;
        this.activateState = activateState;
        this.tradeTypeIconUrl = tradeTypeIconUrl;
    }

    public static TradeTypeResponseDTO from(TradeType tradeType) {
        return TradeTypeResponseDTO.builder()
                .tradeTypeId(tradeType.getTradeTypeId())
                .tradeName(tradeType.getTradeTypeName())
                .tradeTypeIconUrl(tradeType.getTradeTypeIconUrl())
                .activateState(tradeType.getActivateState())
                .build();
    }
}
