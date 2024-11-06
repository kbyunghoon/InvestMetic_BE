package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.lang.Long;
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

    private Long transaction; // 입출금 거래

    private Long dailyProfitLoss; // 일간 손익

    private Integer tradingDays; // 거래일수

    private Long principal; // 원금

    private Long balance; // 잔고

    private Long valuationProfitLoss; // 평가손익

    private Long kpRatio; // KP Ratio

    private Long smScore; // SM Score

    private Long referencePrice; // 기준가

    private Long cumulativeTransactionAmount; // 누적 거래금액

    private Long deposit; // 입금

    private Long cumulativeDeposit; // 누적 입금

    private Long withdrawal; // 출금

    private Long cumulativeWithdrawal; // 누적 출금

    private Long dailyProfitLossRate; // 일간 손익률

    private Long maxDailyProfit; // 최대 일간 이익

    private Long maxDailyProfitRate; // 최대 일간 이익률

    private Long maxDailyLossRate; // 최대 일간 손실률

    private Long totalProfit; // 총 이익

    private Integer profitableDays; // 이익일수

    private Long averageProfit; // 평균 이익

    private Long totalLoss; // 총 손실

    private Integer lossDays; // 손실일수

    private Long averageLoss; // 평균 손실

    private Long cumulativeProfitLoss; // 누적 손익

    private Long cumulativeProfitLossRate; // 누적 손익률

    private Long maxCumulativeProfitLoss; // 최대 누적손익

    private Long maxCumulativeProfitLossRate; // 최대 누적손익률

    private Long averageProfitLoss; // 평균손익

    private Long peakValue; // 최고값

    private Integer daysSincePeak; // 최고값 이후 일수

    private Long currentDrawdown; // 현재 낙폭

    private Long currentDrawdownRate; // 현재 낙폭률

    private Long maxDrawdown; // 최대 낙폭

    private Long maxDrawdownRate; // 최대 낙폭률

    private Double winRate; // 승률

    private Long profitFactor; // Profit Factor

    private Long roa; // ROA

    private Long averageProfitLossRatio; // 평균 손익비율

    private Long coefficientOfVariation; // 변동계수

    private Long sharpeRatio; // Sharpe 비율

    private Long dailyProfitRate; // 일간 손익률

    private Long profitRate; // 손익률
}