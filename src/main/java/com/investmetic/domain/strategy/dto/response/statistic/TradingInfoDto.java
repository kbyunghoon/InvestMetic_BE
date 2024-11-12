package com.investmetic.domain.strategy.dto.response.statistic;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 거래 관련정보
 */
@Getter
@RequiredArgsConstructor
public class TradingInfoDto {
    private final Integer totalTradeDays;        // 총 거래 일수
    private final Integer totalProfitableDays;   // 총 이익 일수
    private final Integer totalLossDays;         // 총 손실 일수
    private final Integer currentConsecutiveLossDays; // 현재 연속 손실 일수
    private final Integer maxConsecutiveProfitDays; // 최대 연속 이익 일수
    private final Integer maxConsecutiveLossDays; // 최대 연속 손실 일수
    private final Double winRate;                // 승률

    public static TradingInfoDto from(StrategyStatistics stats) {
        return new TradingInfoDto(
                stats.getTotalTradeDays(),
                stats.getTotalProfitableDays(),
                stats.getTotalLossDays(),
                stats.getCurrentConsecutiveProfitLossDays(),
                stats.getMaxConsecutiveProfitDays(),
                stats.getMaxConsecutiveLossDays(),
                stats.getWinRate()
        );
    }
}
