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
    private String tradeTypeIconURL;

    public void changeTradeIconURL(String tradeTypeIconURL) {
        this.tradeTypeIconURL = tradeTypeIconURL;
    }

    public void changeActivateState(boolean activateState) {
        this.activateState = activateState;
    }
}