package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockTypeId;

    private String stockTypeName; // 종목명
    private boolean activateState; // 종목 활성 상태

    @Column(length = 1000)
    private String stockTypeIconURL; // 종목아이콘 경로

    public void changeStockTypeIconURL(String stockTypeIconURL) {
        this.stockTypeIconURL = stockTypeIconURL;
    }

    public void changeActivateState(boolean activateState) {
        this.activateState = activateState;
    }
}