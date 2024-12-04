package com.investmetic.domain.strategy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TraderDailyAnalysisRequestDto {
    @NotNull(message = "날짜를 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd") // yyyy-MM-dd 형식으로 지정
    private LocalDate date;

    @NotNull(message = "거래 금액을 입력해주세요.") // Null 값 허용 안 함
    private Long transaction; // 입출금

    @NotNull(message = "일 손익을 입력해주세요.") // Null 값 허용 안 함
    private Long dailyProfitLoss; // 일 손익

    @Builder
    public TraderDailyAnalysisRequestDto(LocalDate date, Long transaction, Long dailyProfitLoss) {
        this.date = date;
        this.transaction = transaction;
        this.dailyProfitLoss = dailyProfitLoss;
    }
}
