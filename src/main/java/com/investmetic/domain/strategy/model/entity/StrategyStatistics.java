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
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class StrategyStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyStatisticsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private Long balance; // 잔고

    private String operationPeriod; // 운용 기간

    private Long cumulativeTransactionAmount; // 누적 거래금액

    private LocalDate startDate; // 시작일

    private Long principal; // 원금

    private LocalDate endDate; // 종료일

    private Integer daysSincePeakUpdate; // 최고점 이후 경과일

    private Long cumulativeProfitAmount; // 누적 수익금액

    private Long cumulativeProfitRate; // 누적 수익률

    private Long recentYearProfitRate; // 최근 1년 수익률

    private Long maxCumulativeProfitAmount; // 최대 누적 수익금액

    private Long maxCumulativeProfitRate; // 최대 누적 수익률

    private Long averageProfitLossAmount; // 평균 손익 금액

    private Long averageProfitLossRate; // 평균 손익률

    private Long maxDailyProfitAmount; // 최대 일간 수익금액

    private Long maxDailyProfitRate; // 최대 일간 수익률

    private Long maxDailyLossAmount; // 최대 일간 손실금액

    private Long maxDailyLossRate; // 최대 일간 손실률

    private Long roa; // ROA

    private Long profitFactor; // Profit Factor

    private Long currentDrawdown; // 현재 낙폭

    private Long currentDrawdownRate; // 현재 낙폭률

    private Long maxDrawdown; // 최대 낙폭

    private Long maxDrawdownRate; // 최대 낙폭률

    private Integer currentConsecutiveProfitLossDays; // 현재 연속 손익일수

    private Integer totalProfitableDays; // 총 이익 일수

    private Integer maxConsecutiveProfitDays; // 최대 연속 이익 일수

    private Integer totalLossDays; // 총 손실 일수

    private Integer maxConsecutiveLossDays; // 최대 연속 손실 일수

    private Double winRate; // 승률

    private Long smScore; // SM Score

    private Long kpRatio; // KP Ratio

    private Long initialInvestment; // 최초 투자금액

    private LocalDate finalProfitLossDate; // 최종 손익 일자

    private Integer totalTradeDays; // 총 거래일수
}