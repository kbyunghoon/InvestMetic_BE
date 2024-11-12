package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.lang.Long;
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

    private String yearMonth; // YYYY-DD

    private Long monthlyAveragePrincipal; //  원금

    private Long depositsWithdrawals; // 입출금 합계

    private Long monthlyProfitLoss; // 월간 손익

    private Long monthlyProfitLossRate; // 월간 손익률

    private Long cumulativeProfitLoss; // 누적 손익

    private Long cumulativeProfitLossRate; // 누적 손익률
}