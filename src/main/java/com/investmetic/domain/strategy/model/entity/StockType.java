package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class StockType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockTypeId;

    private String stockTypeName; // 종목명

    private Boolean activateState; // 종목 활성 상태

    @Column(length = 1000)
    private String stockTypeIconUrl; // 종목아이콘 경로

    public void changeStockTypeIconURL(String stockTypeIconUrl) {
        this.stockTypeIconUrl = stockTypeIconUrl;
    }

    public void changeActivateState() {
        this.activateState = !this.activateState;
    }
}