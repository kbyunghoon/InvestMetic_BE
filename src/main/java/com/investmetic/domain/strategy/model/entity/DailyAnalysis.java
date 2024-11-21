package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAnalysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private LocalDate dailyDate; // 날짜

    private Long transaction; // 입출금 거래

    private Long dailyProfitLoss; // 일간 손익

    private Integer tradingDays; // 거래일수

    private Long principal; // 원금

    private Long balance; // 잔고

    private Long valuationProfitLoss; // 평가손익

    private Double kpRatio; // KP Ratio

    private Double smScore; // SM Score

    private Long referencePrice; // 기준가

    private Long cumulativeTransactionAmount; // 누적 입출금

    private Long deposit; // 입금

    private Long cumulativeDeposit; // 누적 입금

    private Long withdrawal; // 출금

    private Long cumulativeWithdrawal; // 누적 출금

    private Double dailyProfitLossRate; // 일간 손익률

    private Long maxDailyProfit; // 최대 일간 이익

    private Double maxDailyProfitRate; // 최대 일간 이익률

    private Double maxDailyLossRate; // 최대 일간 손실률

    private Long totalProfit; // 총 이익

    private Integer profitableDays; // 이익일수

    private Double averageProfit; // 평균 이익

    private Long totalLoss; // 총 손실

    private Integer lossDays; // 손실일수

    private Long averageLoss; // 평균 손실

    private Long cumulativeProfitLoss; // 누적 손익

    private Double cumulativeProfitLossRate; // 누적 손익률

    private Long maxCumulativeProfitLoss; // 최대 누적손익

    private Double maxCumulativeProfitLossRate; // 최대 누적손익률

    private Long averageProfitLoss; // 평균손익

    private Double averageProfitLossRatio; // 평균손익비율

    private Long peak; // 최고값

    private Long peakRatio; // 최고값 비율

    private Integer daysSincePeak; // 고점후 경과일

    private Long currentDrawdown; // 현재 자본인하금액

    private Double currentDrawdownRate; // 현재 자본인하율

    private Long maxDrawdown; // 최대 자본인하금액

    private Double maxDrawdownRate; // 최대 자본인하율

    private Double winRate; // 승률

    private Double profitFactor; // Profit Factor

    private Double roa; // ROA

    private Double coefficientOfVariation; // 변동계수

    private Double sharpRatio; // Sharp 비율

    @Builder
    public DailyAnalysis(Strategy strategy, LocalDate dailyDate, Long transaction, Long dailyProfitLoss) {
        this.strategy = strategy;
        this.dailyDate = dailyDate;
        this.transaction = transaction;
        this.dailyProfitLoss = dailyProfitLoss;
    }
}