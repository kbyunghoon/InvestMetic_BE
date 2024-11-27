package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.global.util.exceldownload.ExcelColumn;
import com.investmetic.global.util.exceldownload.ExcelSheet;
import com.investmetic.domain.strategy.model.entity.MonthlyAnalysis;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExcelSheet(name = "전략 월간 분석")
public class MonthlyAnalysisResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @ExcelColumn(headerName = "날짜")
    private final String monthlyDate; // YYYY-DD
    @ExcelColumn(headerName = "월평균 원금")
    private final Long monthlyAveragePrincipal; //  월평균 원금
    @ExcelColumn(headerName = "입출금")
    private final Long depositsWithdrawals; // 입출금
    @ExcelColumn(headerName = "월 손익")
    private final Long monthlyProfitLoss; // 월 손익
    @ExcelColumn(headerName = "월 손익률")
    private final Double monthlyProfitLossRate; // 월 손익률
    @ExcelColumn(headerName = "누적손익")
    private final Long cumulativeProfitLoss; // 누적 손익
    @ExcelColumn(headerName = "누적 수익률")
    private final Double cumulativeProfitLossRate; // 누적 수익률


    public static MonthlyAnalysisResponse from(MonthlyAnalysis monthlyAnalysis) {
        return new MonthlyAnalysisResponse(
                monthlyAnalysis.getMonthlyDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                monthlyAnalysis.getMonthlyAveragePrincipal(),
                monthlyAnalysis.getDepositsWithdrawals(),
                monthlyAnalysis.getMonthlyProfitLoss(),
                monthlyAnalysis.getMonthlyProfitLossRate(),
                monthlyAnalysis.getCumulativeProfitLoss(),
                monthlyAnalysis.getCumulativeProfitLossRate()
        );
    }
}
