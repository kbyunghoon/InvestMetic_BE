package com.investmetic.domain.strategy.dto.response.statistic;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssetManagementDto {
    private final Long balance; // 잔고
    private final Long cumulativeTransactionAmount; // 누적 거래금액
    private final Long principal; // 원금
    private final int operationPeriod; // 운용 기간
    private final LocalDate startDate; // 시작일
    private final LocalDate endDate; // 종료일
    private final long daysSincePeakUpdate; // 최고점 이후 경과일

    public static AssetManagementDto from(StrategyStatistics stats) {
        return new AssetManagementDto(
                stats.getBalance(),
                stats.getCumulativeTransactionAmount(),
                stats.getPrincipal(),
                stats.getOperationPeriod(),
                stats.getStartDate(),
                stats.getEndDate(),
                stats.getDaysSincePeakUpdate()
        );
    }
}
