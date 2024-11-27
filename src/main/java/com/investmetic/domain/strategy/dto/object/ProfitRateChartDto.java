package com.investmetic.domain.strategy.dto.object;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"dates", "profitRates"})
public class ProfitRateChartDto {

    private List<String> dates; // x축 데이터 ( YYYY-mm-dd)
    private List<Double> profitRates; // 수익률 리스트

    @Builder
    public ProfitRateChartDto(List<String> dates, List<Double> profitRates) {
        this.dates = dates;
        this.profitRates = profitRates;
    }
}
