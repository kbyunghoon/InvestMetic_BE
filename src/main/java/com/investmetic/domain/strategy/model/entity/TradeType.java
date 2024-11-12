package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeTypeId;
    private String tradeName;
    private boolean activateState;

    @Column(length = 1000)
    private String tradeIconURL;

    public void changeTradeIconURL(String tradeIconURL) {
        this.tradeIconURL = tradeIconURL;
    }

    public void changeActivateState(boolean activateState) {
        this.activateState = activateState;
    }
}