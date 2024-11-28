package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TradeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeTypeId;
    private String tradeTypeName;
    private Boolean activateState;

    @Column(length = 1000)
    private String tradeTypeIconUrl;

    public void changeTradeIconURL(String tradeTypeIconUrl) {
        this.tradeTypeIconUrl = tradeTypeIconUrl;
    }

    public void changeActivateState() {
        this.activateState = !this.activateState;
    }
}