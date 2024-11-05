package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class StockTypeGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockTypeGroupId;

    // 전략과의 다대일 연관 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    // 종목과의 다대일 연관 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_type_id", nullable = false)
    private StockType stockType;

}