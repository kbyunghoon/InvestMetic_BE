package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DailyAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAnalysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private LocalDate date; // 날짜

    private BigDecimal transaction; // 입출금 거래

    private BigDecimal dailyProfitLoss; // 일간 손익

    private Integer tradingDays; // 거래일수

    private BigDecimal principal; // 원금

    private BigDecimal balance; // 잔고

    private BigDecimal valuationProfitLoss; // 평가손익

    private BigDecimal kpRatio; // KP Ratio

    private BigDecimal smScore; // SM Score

    private BigDecimal referencePrice; // 기준가

    private BigDecimal cumulativeTransactionAmount; // 누적 거래금액

    private BigDecimal deposit; // 입금

    private BigDecimal cumulativeDeposit; // 누적 입금

    private BigDecimal withdrawal; // 출금

    private BigDecimal cumulativeWithdrawal; // 누적 출금

    private BigDecimal dailyProfitLossRate; // 일간 손익률

    private BigDecimal maxDailyProfit; // 최대 일간 이익

    private BigDecimal maxDailyProfitRate; // 최대 일간 이익률

    private BigDecimal maxDailyLossRate; // 최대 일간 손실률

    private BigDecimal totalProfit; // 총 이익

    private Integer profitableDays; // 이익일수

    private BigDecimal averageProfit; // 평균 이익

    private BigDecimal totalLoss; // 총 손실

    private Integer lossDays; // 손실일수

    private BigDecimal averageLoss; // 평균 손실

    private BigDecimal cumulativeProfitLoss; // 누적 손익

    private BigDecimal cumulativeProfitLossRate; // 누적 손익률

    private BigDecimal maxCumulativeProfitLoss; // 최대 누적손익

    private BigDecimal maxCumulativeProfitLossRate; // 최대 누적손익률

    private BigDecimal averageProfitLoss; // 평균손익

    private BigDecimal peakValue; // 최고값

    private Integer daysSincePeak; // 최고값 이후 일수

    private BigDecimal currentDrawdown; // 현재 낙폭

    private BigDecimal currentDrawdownRate; // 현재 낙폭률

    private BigDecimal maxDrawdown; // 최대 낙폭

    private BigDecimal maxDrawdownRate; // 최대 낙폭률

    private Double winRate; // 승률

    private BigDecimal profitFactor; // Profit Factor

    private BigDecimal roa; // ROA

    private BigDecimal averageProfitLossRatio; // 평균 손익비율

    private BigDecimal coefficientOfVariation; // 변동계수

    private BigDecimal sharpeRatio; // Sharpe 비율

    private BigDecimal dailyProfitRate; // 일간 손익률

    private BigDecimal profitRate; // 손익률
}