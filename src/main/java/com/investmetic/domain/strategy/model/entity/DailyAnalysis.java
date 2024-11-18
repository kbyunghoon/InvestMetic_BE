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

    private Long transaction = 0L; // 입출금 거래

    private Long dailyProfitLoss = 0L; // 일간 손익

    private Integer tradingDays; // 거래일수

    private Long principal = 0L; // 원금

    private Long balance = 0L; // 잔고

    private Long valuationProfitLoss = 0L; // 평가손익

    private Double kpRatio = 0.0; // KP Ratio

    private Double smScore = 0.0; // SM Score

    private double referencePrice = 0.0; // 기준가

    private Long cumulativeTransactionAmount = 0L; // 누적 입출금

    private Long deposit = 0L; // 입금

    private Long cumulativeDeposit = 0L; // 누적 입금

    private Long withdrawal = 0L; // 출금

    private Long cumulativeWithdrawal = 0L; // 누적 출금

    private Double dailyProfitLossRate = 0.0; // 일간 손익률

    private Long maxDailyProfit = 0L; // 최대 일간 이익

    private Double maxDailyProfitRate = 0.0; // 최대 일간 이익률

    private Double maxDailyLossRate = 0.0; // 최대 일간 손실률

    private Long totalProfit = 0L; // 총 이익

    private int profitableDays = 0; // 이익일수

    private Long averageProfit = 0L; // 평균 이익

    private Long totalLoss = 0L; // 총 손실

    private int lossDays = 0; // 손실일수

    private Long averageLoss = 0L; // 평균 손실

    private Long cumulativeProfitLoss = 0L; // 누적 손익

    private Double cumulativeProfitLossRate = 0.0; // 누적 손익률

    private Long maxCumulativeProfitLoss = 0L; // 최대 누적손익

    private Double maxCumulativeProfitLossRate = 0.0; // 최대 누적손익률

    private Long averageProfitLoss = 0L; // 평균손익비율

    private Double averageProfitLossRatio = 0.0; // 평균손익비율

    private Long peak = 0L; // 최고값

    private Double peakRatio = 0.0; // 최고값 비율

    private int daysSincePeak = 0; // 고점후 경과일

    private Long currentDrawdown = 0L; // 현재 자본인하금액

    private Double currentDrawdownRate = 0.0; // 현재 자본인하율

    private Long maxDrawdown = 0L; // 최대 자본인하금액

    private Double maxDrawdownRate = 0.0; // 최대 자본인하율

    private Double winRate = 0.0; // 승률

    private Double profitFactor = 0.0; // Profit Factor

    private Double roa = 0.0; // ROA

    private Double coefficientOfVariation = 0.0; // 변동계수

    private Double sharpRatio = 0.0; // Sharp 비율

    @Builder
    public DailyAnalysis(Long dailyAnalysisId, Long principal, Double dailyProfitLossRate, Long deposit,
                         Long withdrawal,
                         Long balance, Long cumulativeProfitLoss, Double cumulativeProfitLossRate, Strategy strategy,
                         LocalDate dailyDate, Long transaction, Long dailyProfitLoss, Long valuationProfitLoss,
                         double referencePrice, Long cumulativeDeposit, Long cumulativeWithdrawal, Long maxDailyProfit,
                         Double maxDailyProfitRate, Double maxDailyLossRate, Long cumulativeTransactionAmount,
                         Long totalProfit, int profitableDays, Long averageProfit, Long totalLoss,
                         int lossDays, Long averageLoss, Long maxCumulativeProfitLoss,
                         Double maxCumulativeProfitLossRate,
                         Long averageProfitLoss, Double averageProfitLossRatio, Long peak, Double peakRatio,
                         int daysSincePeak, Long currentDrawdown, Double currentDrawdownRate, Long maxDrawdown,
                         Double maxDrawdownRate, Double winRate, Double profitFactor, Double roa,
                         Double coefficientOfVariation, Double sharpRatio) {
        this.dailyAnalysisId = dailyAnalysisId;
        this.principal = principal;
        this.dailyProfitLossRate = dailyProfitLossRate;
        this.deposit = deposit;
        this.withdrawal = withdrawal;
        this.balance = balance;
        this.cumulativeProfitLoss = cumulativeProfitLoss;
        this.cumulativeProfitLossRate = cumulativeProfitLossRate;
        this.strategy = strategy;
        this.dailyDate = dailyDate;
        this.transaction = transaction;
        this.dailyProfitLoss = dailyProfitLoss;
        this.valuationProfitLoss = valuationProfitLoss;
        this.referencePrice = referencePrice;
        this.cumulativeDeposit = cumulativeDeposit;
        this.cumulativeWithdrawal = cumulativeWithdrawal;
        this.maxDailyProfit = maxDailyProfit;
        this.maxDailyProfitRate = maxDailyProfitRate;
        this.maxDailyLossRate = maxDailyLossRate;
        this.cumulativeTransactionAmount = cumulativeTransactionAmount;
        this.totalProfit = totalProfit;
        this.profitableDays = profitableDays;
        this.averageProfit = averageProfit;
        this.totalLoss = totalLoss;
        this.lossDays = lossDays;
        this.averageLoss = averageLoss;
        this.maxCumulativeProfitLoss = maxCumulativeProfitLoss;
        this.maxCumulativeProfitLossRate = maxCumulativeProfitLossRate;
        this.averageProfitLoss = averageProfitLoss;
        this.averageProfitLossRatio = averageProfitLossRatio;
        this.peak = peak;
        this.peakRatio = peakRatio;
        this.daysSincePeak = daysSincePeak;
        this.currentDrawdown = currentDrawdown;
        this.currentDrawdownRate = currentDrawdownRate;
        this.maxDrawdown = maxDrawdown;
        this.maxDrawdownRate = maxDrawdownRate;
        this.winRate = winRate;
        this.profitFactor = profitFactor;
        this.roa = roa;
        this.coefficientOfVariation = coefficientOfVariation;
        this.sharpRatio = sharpRatio;
    }
}