package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAnalysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Builder.Default
    private LocalDate dailyDate = LocalDate.now(); // 날짜

    @Builder.Default
    private Long transaction = 0L; // 입출금 거래

    @Builder.Default
    private Long dailyProfitLoss = 0L; // 일간 손익

    @Builder.Default
    private Integer tradingDays = 0; // 거래일수

    @Builder.Default
    private Long principal = 0L; // 원금

    @Builder.Default
    private Long balance = 0L; // 잔고

    @Builder.Default
    private Long valuationProfitLoss = 0L; // 평가손익

    @Builder.Default
    private Double kpRatio = 0.0; // KP Ratio

    @Builder.Default
    private Double smScore = 0.0; // SM Score

    @Builder.Default
    private Double referencePrice = 0.0; // 기준가

    @Builder.Default
    private Long cumulativeTransactionAmount = 0L; // 누적 입출금

    @Builder.Default
    private Long deposit = 0L; // 입금

    @Builder.Default
    private Long cumulativeDeposit = 0L; // 누적 입금

    @Builder.Default
    private Long withdrawal = 0L; // 출금

    @Builder.Default
    private Long cumulativeWithdrawal = 0L; // 누적 출금

    @Builder.Default
    private Double dailyProfitLossRate = 0.0; // 일간 손익률

    @Builder.Default
    private Long maxDailyProfit = 0L; // 최대 일간 이익

    @Builder.Default
    private Double maxDailyProfitRate = 0.0; // 최대 일간 이익률

    @Builder.Default
    private Double maxDailyLossRate = 0.0; // 최대 일간 손실률

    @Builder.Default
    private Long totalProfit = 0L; // 총 이익

    @Builder.Default
    private Long profitableDays = 0L; // 이익일수

    @Builder.Default
    private Long averageProfit = 0L; // 평균 이익

    @Builder.Default
    private Long totalLoss = 0L; // 총 손실

    @Builder.Default
    private Long lossDays = 0L; // 손실일수

    @Builder.Default
    private Long averageLoss = 0L; // 평균 손실

    @Builder.Default
    private Long cumulativeProfitLoss = 0L; // 누적 손익

    @Builder.Default
    private Double cumulativeProfitLossRate = 0.0; // 누적 손익률

    @Builder.Default
    private Long maxCumulativeProfitLoss = 0L; // 최대 누적손익

    @Builder.Default
    private Double maxCumulativeProfitLossRate = 0.0; // 최대 누적손익률

    @Builder.Default
    private Long averageProfitLoss = 0L; // 평균 손익비율

    @Builder.Default
    private Double averageProfitLossRatio = 0.0; // 평균 손익비율

    @Builder.Default
    private Long peak = 0L; // 최고값

    @Builder.Default
    private Double peakRatio = 0.0; // 최고값 비율

    @Builder.Default
    private Long daysSincePeak = 0L; // 고점 후 경과일

    @Builder.Default
    private Long currentDrawdown = 0L; // 현재 자본인하금액

    @Builder.Default
    private Double currentDrawdownRate = 0.0; // 현재 자본인하율

    @Builder.Default
    private Long drawDownPeriod = 0L; // DD day

    @Builder.Default
    private Long maxDrawdown = 0L; // 최대 자본인하금액

    @Builder.Default
    private Double maxDrawdownRate = 0.0; // 최대 자본인하율

    @Builder.Default
    private Double winRate = 0.0; // 승률

    @Builder.Default
    private Double profitFactor = 0.0; // Profit Factor

    @Builder.Default
    private Double roa = 0.0; // ROA

    @Builder.Default
    private Double coefficientOfVariation = 0.0; // 변동계수

    @Builder.Default
    private Double sharpRatio = 0.0; // Sharp 비율

    @Builder.Default
    private Double maxDrawDownInRate = 0.0; //

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Proceed proceed = Proceed.YES; // 등록, 수정 시 NO

    public void modifyDailyAnalysis(Long transaction, Long dailyProfitLoss) {
        this.transaction = transaction;
        this.dailyProfitLoss = dailyProfitLoss;
    }
}
