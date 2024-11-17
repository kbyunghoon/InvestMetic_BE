package com.investmetic.domain.strategy.dto.request;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TraderDailyAnalysisRequestDto {
    private LocalDate date;
    private Long transaction; // 입출금
    private Long dailyProfitLoss; // 일 손익

    @Builder
    public TraderDailyAnalysisRequestDto(LocalDate date, Long transaction, Long dailyProfitLoss) {
        this.date = date;
        this.transaction = transaction;
        this.dailyProfitLoss = dailyProfitLoss;
    }
}
