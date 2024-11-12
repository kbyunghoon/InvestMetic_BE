package com.investmetic.domain.strategy.dto.response.statistic;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 전략 통계 조회 응답 DTO
 */
@Getter
@RequiredArgsConstructor
public class StrategyStatisticsResponse {
    private final AssetManagementDto assetManagement;   // 자산 및 운영정보
    private final ProfitLossDto profitLoss;             // 손익률 관련정보
    private final DdMddInfoDto ddMddInfo;               // DD 및 MDD 관련 정보
    private final TradingInfoDto tradingInfo;           // 거래관련 정보

    public static StrategyStatisticsResponse from(StrategyStatistics stats) {
        return new StrategyStatisticsResponse(
                AssetManagementDto.from(stats),
                ProfitLossDto.from(stats),
                DdMddInfoDto.from(stats),
                TradingInfoDto.from(stats)
        );
    }
}
