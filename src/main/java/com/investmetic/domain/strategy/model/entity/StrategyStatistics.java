package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyStatisticsId;

    private Long balance; // 잔고

    private Integer operationPeriod; // 운용 기간(일수로 변경)

    private Long cumulativeTransactionAmount; // 누적 입출금액

    private LocalDate startDate; // 시작일

    private Long principal; // 원금

    private LocalDate endDate; // 종료일

    private Integer daysSincePeakUpdate; // 최고점 이후 경과일

    private Long cumulativeProfitAmount; // 누적 수익금액

    private Double cumulativeProfitRate; // 누적 수익률

    private Double recentYearProfitRate; // 최근 1년 수익률

    private Long maxCumulativeProfitAmount; // 최대 누적 수익금액

    private Double maxCumulativeProfitRate; // 최대 누적 수익률

    private Long averageProfitLossAmount; // 평균 손익 금액

    private Double averageProfitLossRate; // 평균 손익률

    private Long maxDailyProfitAmount; // 최대 일수익 금액

    private Double maxDailyProfitRate; // 최대 일 수익률

    private Long maxDailyLossAmount; // 최대 일손실 금액

    private Double maxDailyLossRate; // 최대 일 손실률

    private Double roa; // ROA

    private Double profitFactor; // Profit Factor

    private Long currentDrawdown; // 현재 자본 인하 금액

    private Double currentDrawdownRate; // 현재 자본 인하율

    private Long maxDrawdown; // 최대 자본 인하 금액

    private Double maxDrawdownRate; // 최대 자본 인하율

    private Integer currentConsecutiveProfitLossDays; // 현재 연속 손익일수

    private Integer totalProfitableDays; // 총 이익 일수

    private Integer maxConsecutiveProfitDays; // 최대 연속 이익 일수

    private Integer totalLossDays; // 총 손실 일수

    private Integer maxConsecutiveLossDays; // 최대 연속 손실 일수

    private Double winRate; // 승률

    private Double smScore; // SM Score

    private Double kpRatio; // KP Ratio

    private Long initialInvestment; // 최초 투자금액

    private LocalDate finalProfitLossDate; // 최종 손익 일자

    private Integer totalTradeDays; // 총 매매일수
}