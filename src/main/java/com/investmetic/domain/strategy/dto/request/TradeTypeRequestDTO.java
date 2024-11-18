package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.TradeType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeTypeRequestDTO {
    private String tradeTypeName;

    @Column(length = 1000)
    private String tradeTypeIconURL;
    private int size;

    @Builder
    public TradeTypeRequestDTO(String tradeTypeName, String tradeTypeIconURL, int size) {
        this.tradeTypeName = tradeTypeName;
        this.tradeTypeIconURL = tradeTypeIconURL;
        this.size = size;
    }

    // DTO -> Entity 변환 메서드
    public TradeType toEntity() {
        return TradeType.builder()
                .tradeTypeName(tradeTypeName)
                .activateState(true) // 기본값으로 활성 상태 설정
                .tradeTypeIconURL(tradeTypeIconURL)
                .build();
    }

}
