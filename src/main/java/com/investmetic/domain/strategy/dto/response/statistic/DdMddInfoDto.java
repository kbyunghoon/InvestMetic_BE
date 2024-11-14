package com.investmetic.domain.strategy.dto.response.statistic;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DD 및 MDD 관련정보
 */
@Getter
@RequiredArgsConstructor
public class DdMddInfoDto {
    private final Long currentDrawdown;          // 현재 자본 인하 금액
    private final Double currentDrawdownRate;    // 현재 자본 인하율
    private final Long maxDrawdown;              // 최대 자본 인하 금액
    private final Double maxDrawdownRate;          // 최대 자본 인하율

    // 최대 자본 인하율
    public static DdMddInfoDto from(StrategyStatistics stats) {
        return new DdMddInfoDto(
                stats.getCurrentDrawdown(),
                stats.getCurrentDrawdownRate(),
                stats.getMaxDrawdown(),
                stats.getMaxDrawdownRate()
        );
    }

}
