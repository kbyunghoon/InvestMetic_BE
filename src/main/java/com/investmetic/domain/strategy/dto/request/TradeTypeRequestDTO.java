package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.TradeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeTypeRequestDTO {
    private String tradeTypeName;
    private String tradeIconURL;
    private int size;

    @Builder
    public TradeTypeRequestDTO(String tradeTypeName, String tradeIconURL, int size) {
        this.tradeTypeName = tradeTypeName;
        this.tradeIconURL = tradeIconURL;
        this.size = size;
    }

    // DTO -> Entity 변환 메서드
    public TradeType toEntity() {
        return TradeType.builder()
                .tradeTypeName(tradeTypeName)
                .activateState(true) // 기본값으로 활성 상태 설정
                .tradeIconURL(tradeIconURL)
                .build();
    }
}
