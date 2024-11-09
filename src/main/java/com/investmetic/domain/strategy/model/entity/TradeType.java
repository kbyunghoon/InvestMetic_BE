package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TradeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeTypeId;
    private String tradeName;
    private boolean activate_state;

    @Column(length = 1000)
    private String tradeIconPath;

    @Builder
    public TradeType(Long tradeTypeId, String tradeName, boolean activate_state, String tradeIconPath) {
        this.tradeTypeId = tradeTypeId;
        this.tradeName = tradeName;
        this.activate_state = activate_state;
        this.tradeIconPath = tradeIconPath;
    }
}