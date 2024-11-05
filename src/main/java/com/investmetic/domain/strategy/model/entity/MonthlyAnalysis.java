package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MonthlyAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyAnalysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private String month; // 월

    private BigDecimal monthlyAveragePrincipal; // 월 평균 원금

    private BigDecimal depositsWithdrawals; // 입출금 합계

    private BigDecimal monthlyProfitLoss; // 월간 손익

    private BigDecimal monthlyProfitLossRate; // 월간 손익률

    private BigDecimal cumulativeProfitLoss; // 누적 손익

    private BigDecimal cumulativeProfitLossRate; // 누적 손익률
}