package com.investmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"dates", "profitRates"})
public class ProfitRateChartDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private List<LocalDate> dates; // x축 데이터 ( YYYY-mm-dd)
    private List<Double> profitRates; // 수익률 리스트

    @Builder
    public ProfitRateChartDto(List<LocalDate> dates, List<Double> profitRates) {
        this.dates = dates;
        this.profitRates = profitRates;
    }
}
