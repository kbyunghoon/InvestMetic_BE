package com.investmetic.domain.strategy.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class TotalRateDto {
    Long totalInvester;
    Long totalTrader;
    Long totalStrategies;
    Long totalSubscribe;

}
